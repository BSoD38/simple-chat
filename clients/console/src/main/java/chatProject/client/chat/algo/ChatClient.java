package chatProject.client.chat.algo;

import chatProject.AddMessageForm;
import chatProject.FakeInstances;
import chatProject.algo.ChatroomAlgo;
import chatProject.algo.MessageAlgo;
import chatProject.algo.UserAlgo;
import chatProject.model.listener.ChatroomsListener;
import chatProject.model.listener.MessageListener;
import chatProject.model.listener.UserListener;
import chatProject.model.messages.ChatInstance;
import chatProject.model.messages.Chatroom;
import chatProject.model.messages.Message;
import chatProject.model.user.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class ChatClient<T> implements UserAlgo, ChatroomAlgo<T>, MessageAlgo<T>, AutoCloseable {

    private final ChatInstance<T> chatInstance;
    private UserInfo currentUser;

    private final Map<Integer, Collection<MessageListener<T>>> messageListeners;
    private final Collection<ChatroomsListener<T>> chatroomListeners;
    private final Collection<UserListener> usersListeners;

    private final String serverUrl;

    private final Gson json = new Gson();

    private SocketReader<T> socketListener = null;

    private Thread pingThread = null;

    public ChatClient(ChatInstance<T> chatInstance,
                      UserInfo currentUser,
                      Map<Integer, Collection<MessageListener<T>>> messageListeners,
                      Collection<ChatroomsListener<T>> chatroomListeners,
                      Collection<UserListener> usersListeners,
                      String serverUrl) {
        this.chatInstance = chatInstance;
        this.currentUser = currentUser;
        this.messageListeners = messageListeners;
        this.chatroomListeners = chatroomListeners;
        this.usersListeners = usersListeners;
        this.serverUrl = serverUrl;
    }

    public static <T> ChatClient<T> initEmptyChat(
            String hostname,
            int webServerPort,
            int socketPort) {

        final String serverUrl = "http://" + hostname + ':' + webServerPort + '/';

        final ChatClient<T> client = new ChatClient<>(
                ChatInstance.initEmptyChat(),
                FakeInstances.UNKNOWN_USER_INFO,
                new HashMap<>(),
                new HashSet<>(),
                new HashSet<>(),
                serverUrl
        );

        client.socketListener = new SocketReader<> (
                hostname,
                socketPort,
                client,
                client.json
        );
        client.socketListener.start();

        client.pingThread = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(1000); // ping every 1s
                    if (client.currentUser != null
                            && !client.currentUser.equals(FakeInstances.UNKNOWN_USER_INFO)) {
                        client.login(client.currentUser.getAccount().getUsername());
                    }
                } catch (InterruptedException e) {
                    // interrupted
                    break;
                }
            }
        });
        client.pingThread.start();

        return client;
    }

    @Override
    public void close() throws IOException {
        // close all threads on exit

        pingThread.interrupt();
        // cleanly close the socket on exit
        socketListener.closeSocket();
        socketListener.interrupt();
    }

    /* **************************** User part *********************/

    /**
     * Gets the current user of the client.
     * @return the current user in the class
     */
    public UserInfo getCurrentUser() {
        return currentUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserInfo login(String userName) {

        final String response;
        try {
            response = Request.Post(serverUrl + "/login").bodyByteArray(userName.getBytes())
                    .execute().returnContent().asString();
        } catch (IOException e) {
            System.err.println("Cannot login " + userName);
            return FakeInstances.UNKNOWN_USER_INFO;
        }

        final UserInfo user =
                Optional.ofNullable(response)
                        .map(resp -> json.fromJson(resp, UserInfo.class))
                        .orElse(FakeInstances.UNKNOWN_USER_INFO);
        this.currentUser = user;
        return user;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<UserInfo> getUsers() {

        try {
            final String response = Request.Get(serverUrl + "/users")
                    .execute().returnContent().asString();

            // we need a Type token to deserialize a list with a parametrized type
            // avoids cast issues from Tree to concrete class
            Type listOfUsers = new TypeToken<Collection<UserInfo>>() {}.getType();
            return json.fromJson(response, listOfUsers);
        } catch (IOException e) {
            throw new RuntimeException("Cannot get users", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserInfo notifyUserChange(UserInfo user) {
        if (chatInstance.addUser(user)) {
            usersListeners.forEach(
                    userListener -> userListener.notifyUserChange(user)
            );
        }
        return user;
    }

    /**
     * Adds a new listener on user changes.
     * @param listener the listener to add
     */
    public void addUserListener(UserListener listener) {
        usersListeners.add(listener);
    }

    /* **************************** Chatroom part *********************/

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getCurrentChatroomNames() {
        try {
            Type listOfString = new TypeToken<List<String>>() {}.getType();
            return json.fromJson(
                    Request.Get(serverUrl + "/chatrooms")
                            .execute().returnContent().asString(),
                    listOfString);
        } catch (IOException e) {
            throw new RuntimeException("Cannot get chatroom names", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Chatroom<T> getChatroom(int chatroomId) {
        try {
            Type chatroomT = new TypeToken<Chatroom<T>>() {}.getType();
            return json.fromJson(
                    Request.Get(serverUrl + "/chatroom/" + chatroomId)
                            .execute().returnContent().asString(),
                    chatroomT);
        } catch (IOException e) {
            throw new RuntimeException("Cannot get chatroom " + chatroomId, e);
        }
    }

    /**
     * Gets the name of a chatroom given its ID.
     * @param chatroomId the chatroom ID
     * @return the chatroom name
     */
    public String getChatroomName(int chatroomId) {
        return getChatroom(chatroomId).getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addChatroom(String chatroomName, UserInfo owner) {
        try {
            final String response = Request.Put(serverUrl + "/chatroom/" + chatroomName).bodyString(
                    json.toJson(owner),
                    ContentType.APPLICATION_JSON
            ).execute().returnContent().asString();
            return json.fromJson(response, Integer.class);
        } catch (IOException e) {
            System.err.println("Cannot add chatroom");
            return -1;
        }
    }

    /**
     * Creates a new chatroom using the current user of the client as the owner.
     * @param chatroomName the name of the chatroom to create
     */
    public void createChatroomFromCurrentUser(String chatroomName) {
        addChatroom(chatroomName, currentUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Chatroom<T> notifyNewChatroom(Chatroom<T> newChatroom) {
        chatroomListeners.forEach(
                listener -> listener.notifyNewChatroom(newChatroom)
        );
        return newChatroom;
    }

    /**
     * Adds a new listener on chatroom changes.
     * @param listener the listener to add
     */
    public void addChatroomListener(ChatroomsListener<T> listener) {
        chatroomListeners.add(listener);
    }

    /* **************************** Messages part *********************/

    /**
     * Sends a new message in a chatroom using the current user of the client.
     * @param chatroomId the ID of the chatroom where the message is sent
     * @param message the content of the message to send
     */
    public void sendMessageForCurrentUser(int chatroomId, T message) {
        addMessage(chatroomId, currentUser, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message<T> addMessage(int chatroomId, UserInfo currentUser, T content) {

        final String response;
        try {
            response = Request.Put(serverUrl + "/message")
                    .bodyString(
                            json.toJson(new AddMessageForm<>(chatroomId, currentUser, content.toString())),
                            ContentType.APPLICATION_JSON
                    ).execute().returnContent().asString();
            Type messageT = new TypeToken<Message<T>>() {}.getType();
            return json.fromJson(response, messageT);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to add the message in chatroom " + chatroomId
                            + " for user " + currentUser
                            + " with content " + content,
                    e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Message<T>> getChatroomMessages(int chatroomId) {

        final String response;
        try {
            response = Request.Get(serverUrl + "/messages/" + chatroomId)
                    .execute().returnContent().asString();

            // we need a Type token to deserialize a list with a parametrized type
            // avoids cast issues from Tree to concrete class
            Type listOfMessages = new TypeToken<List<Message<T>>>() {}.getType();
            return json.fromJson(response, listOfMessages);
        } catch (IOException e) {
            throw new RuntimeException("Cannot get chatroom messages for " + chatroomId, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message<T> notifyNewMessage(int chatroomId, Message<T> message) {
        Optional.ofNullable(
                messageListeners.get(chatroomId)
        ).ifPresent(listeners -> listeners.forEach(
                listener -> listener.notifyNewMessage(chatroomId, message)
                )
        );
        return message;
    }

    /**
     * Adds a new listener on message changes in a chatroom.
     * @param chatroomId the chatroom to listen to
     * @param listener the listener to add
     */
    public void addMessageListener(int chatroomId, MessageListener<T> listener) {
        final Collection<MessageListener<T>> currentListeners =
                Optional.ofNullable(this.messageListeners.get(chatroomId))
                        .orElseGet(HashSet::new);

        currentListeners.add(listener);
        messageListeners.put(chatroomId, currentListeners);
    }

}
