package chatProject.server;

import chatProject.algo.ChatroomAlgo;
import chatProject.algo.MessageAlgo;
import chatProject.algo.UserAlgo;
import chatProject.model.messages.ChatInstance;
import chatProject.model.messages.Chatroom;
import chatProject.model.messages.Message;
import chatProject.model.user.UserInfo;
import chatProject.model.user.Status;
import chatProject.model.user.UserAccount;
import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class implements the server side of the Chat.
 * To be generated in the {@link Main} instance using {@link #initEmptyChat(int, Gson)}.
 * @param <T> the type of messages to use (probably String)
 */
public class ChatServer<T> implements UserAlgo, ChatroomAlgo<T>, MessageAlgo<T>, AutoCloseable {

    /**
     * The model for the chat.
     */
    private final ChatInstance<T> chatInstance;

    /**
     * The current list of connected clients.
     */
    private final Collection<ClientNotifierInterface<T>> clientNotifiers;

    /**
     * A shared Json (de)serializer to improve performance.
     */
    private final Gson json;

    /**
     * The thread that holds a socket to send notifications of new messages and new chatrooms to clients.
     * Must be interrupted on close.
     */
    private Thread socketThread = null;

    /**
     * The thread that checks for idle clients.
     * Must be interrupted on close.
     */
    private Thread checkIdleClients = null;

    public ChatServer(ChatInstance<T> chatInstance,
                      Collection<ClientNotifierInterface<T>> clientNotifiers,
                      Gson json) {
        this.chatInstance = chatInstance;
        this.clientNotifiers = clientNotifiers;
        this.json = json;
    }

    /**
     * The entry point to generate an instance of this class using an empty {@link ChatInstance} model.
     * @param socketPort the port of the socket to open on this server.
     * @param json the Json (de)serializer to use
     * @param <T> the type of messages to use
     * @return a new instance of this class to use as a server
     * @throws IOException not sure when ?
     */
    public static <T> ChatServer<T> initEmptyChat(int socketPort, Gson json) throws IOException {

        // instantiate a new instance of this class with an empty model.
        final ChatServer<T> server = new ChatServer<>(
                ChatInstance.initEmptyChat(),
                new HashSet<>(),
                json);

        // open a dedicated thread to manage the socket for notifications.
        final Thread socketThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.openSocket(socketPort);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to open new socket on port " + socketPort, e);
                }
            }
        });
        server.socketThread = socketThread;

        //TODO: I should start the socket thread here

        server.checkIdleClients();

        return server;
    }

    /**
     * Opens a socket on the given port to notify clients of new chatrooms and messages.
     * @param port the port to use
     * @throws IOException if the socket cannot be opened
     */
    public void openSocket(int port) throws IOException {

        // open the socket in a try-with-resources (auto close the socket on exit)
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            // loop forever to accept all new clients
            while(true) {

                // Socket.accept() is blocking - wait for a new client
                final Socket client = serverSocket.accept();
                // a new client has been found
                clientNotifiers.add(
                        // init the notifier to send notifs for this new client
                        ClientNotifier.init(client, json)
                );
            }
        }
    }

    /**
     * Checks for idle clients (no ping for a long time).
     * Updates the status of these users accordingly.
     */
    public void checkIdleClients() {
        this.checkIdleClients = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(100); // check every 100ms
                    // avoid removing instances during the iteration - store members to update
                    final Collection<UserInfo> usersToUpdate = new HashSet<>();
                    chatInstance.getUsers().forEach( (user, time) -> {
                                if (user.getCurrentStatus() == Status.ACTIVE
                                        && ChronoUnit.SECONDS.between(time, LocalDateTime.now()) > 2) {
                                    user.setCurrentStatus(Status.INACTIVE);
                                    usersToUpdate.add(user);
                                }
                            }
                    );
                    usersToUpdate.forEach(this::notifyUserChange);
                } catch (InterruptedException e) {
                    // interrupted
                    break;
                }
            }
        }
        );

        this.checkIdleClients.start();

    }

    @Override
    public void close() {

        /* 1. we should end infinite loops before closing... */

        // 2. terminate all threads :

        // cleanly close the check for idle clients
        checkIdleClients.interrupt();
        // cleanly close the socket on exit
        socketThread.interrupt();
    }

    /* **************************** User part *********************/

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<UserInfo> getUsers() {
        return chatInstance.getUsers().keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserInfo login(String userName) {
        final UserInfo user = new UserInfo(
                findUser(userName).orElse(new UserAccount(0, "test")),
                Status.ACTIVE // user just logged in - status is active
        );
        notifyUserChange(user);

        return user;
    }

    /**
     * Finds a user in the model given its username (if the user is already registered).
     * @param userName the username to find
     * @return an optional {@link UserAccount} with the user model only if already in the model
     */
    public Optional<UserAccount> findUser(String userName) {
        // Test code
        if (userName.equals("testUser")) {
            return Optional.of(new UserAccount(0, userName));
        } else {
            return Optional.empty();
        }
        // Real code
        /*
        return chatInstance.getUsers().keySet().stream()
                .map(UserInfo::getAccount)
                .filter(account -> account.getUsername().equals(userName))
                .findAny();
        */
    }

    /**
     * Gets the list of users in the model with an active status.
     * @return the list of connected users
     */
    public Collection<String> getConnectedUsers() {
        return Optional.ofNullable(getUsers())
                // get all users in the model
                .map(users -> users.parallelStream()
                        // filter to get only active users
                        .filter(user -> user.getCurrentStatus() == Status.ACTIVE)
                        // get username(s) from user models
                        .map(user -> user.getAccount().getUsername())
                        // collect results
                        .collect(Collectors.toSet()))
                // if getUsers() returns null - return an empty set
                .orElse(Collections.emptySet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserInfo notifyUserChange(UserInfo user) {
        // notify a user change only if the user did change
        if (chatInstance.addUser(user)) {
            // notify all clients
            clientNotifiers.forEach(
                    client -> client.notifyUserChange(user)
            );
        }
        return user;
    }

    /* **************************** Chatroom part *********************/

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getCurrentChatroomNames() {
        return chatInstance
                // retrieve all chatrooms
                .getCurentChatrooms()
                .stream()
                // get the chatroom name(s) from the model(s) instance(s)
                .map(Chatroom::getName)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Chatroom<T> getChatroom(int chatroomId) {
        return chatInstance.getCurentChatrooms().get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addChatroom(String chatroomName, UserInfo owner) {

        // instantiate the chatroom
        final Chatroom<T> newChatroom = new Chatroom<>(chatroomName, owner, new ArrayList<>());

        // add it in the model
        final int newChatroomId = chatInstance.addChatroom(newChatroom);

        /* maybe I should notify clients about the new chatroom ?? */

        return newChatroomId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Chatroom<T> notifyNewChatroom(Chatroom<T> newChatroom) {
        if (clientNotifiers != null) {
            clientNotifiers.forEach(
                    client -> client.notifyNewChatroom(newChatroom)
            );
        }
        return newChatroom;
    }

    /* **************************** Messages part *********************/

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Message<T>> getChatroomMessages(int chatroomId) {
        return Optional.ofNullable(getChatroom(chatroomId))
                .get()
                .getCurrentMessages();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message<T> addMessage(int chatroomId, UserInfo user, T content) {
        Message<T> newMessage = getChatroom(chatroomId).addMessage(user, content);

        // return new created message
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message<T> notifyNewMessage(int chatroomId, Message<T> newMessage) {

        clientNotifiers.forEach(
                client -> client.notifyNewMessage(chatroomId, newMessage)
        );
        return newMessage;
    }

}
