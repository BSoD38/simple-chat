package chatProject.client.chat.algo;

import chatProject.model.messages.Chatroom;
import chatProject.model.messages.Message;
import chatProject.model.user.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.Socket;

/**
 * A dedicated class to manage the socket that sends and receives notifications from and to the server.
 * @param <T> the type of messages to use (probably String)
 */
public class SocketReader<T> extends Thread {

    private final String serverHostname;
    private final int serverSocketPort;
    private final ChatClient<T> chatClient;
    private final Gson json;

    private Socket socket;

    public SocketReader(String serverHostname, int serverSocketPort, ChatClient<T> chatClient, Gson json) {
        this.serverHostname = serverHostname;
        this.serverSocketPort = serverSocketPort;
        this.chatClient = chatClient;
        this.json = json;
    }

    public void run() {
        try {
            this.socket = new Socket(serverHostname, serverSocketPort);
        } catch (IOException | RuntimeException e) {
            System.err.println("Unable to connect to host : " + serverHostname + ":" + serverSocketPort);
            System.exit(2);
        }
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            while (true) {
                try {
                    String response = reader.readLine();
                    if (response != null && !response.isEmpty()) {
                        final int code = Character.getNumericValue(response.charAt(0));
                        switch (code) {
                            case 0:
                                // ADD CHATROOM
                                Type chatroomT = new TypeToken<Chatroom<T>>() {}.getType();
                                final Chatroom<T> chatroom = json.fromJson(
                                        response.substring(1),
                                        chatroomT);
                                chatClient.notifyNewChatroom(chatroom);
                                break;
                            case 1:
                                // NEW MESSAGE
                                final int chatroomId = Character.getNumericValue(response.charAt(1));
                                Type messageT = new TypeToken<Message<T>>() {}.getType();
                                final Message<T> msg = json.fromJson(
                                        response.substring(2),
                                        messageT
                                );
                                chatClient.notifyNewMessage(chatroomId, msg);
                                break;
                            case 2:
                                // USER CHANGED
                                final UserInfo user = json.fromJson(
                                        response.substring(1),
                                        UserInfo.class
                                );
                                chatClient.notifyUserChange(user);
                            default:
                                break;
                        }

                    }
                } catch (IOException ex) {
                    // socket closed
                    return;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to open new socket to reach " + serverHostname + " on port " + serverSocketPort);
        }
    }

    /**
     * Closes the socket.
     * Must be called on exit
     * @throws IOException if the socket cannot be closed
     */
    public void closeSocket() throws IOException {
        if (this.socket != null && !this.socket.isClosed()) {
            this.socket.close();
        }
    }
}
