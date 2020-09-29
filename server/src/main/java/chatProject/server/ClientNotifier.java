package chatProject.server;

import chatProject.model.messages.Chatroom;
import chatProject.model.messages.Message;
import chatProject.model.user.UserInfo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A class to ease sending notifications to connected clients of this server.
 * Have a look to the {@code SocketReader} class in the {@code ChatClient} class for the socket readers.
 * @param <T> the type of messages to use
 */
public class ClientNotifier<T> implements ClientNotifierInterface<T> {

    private final PrintWriter writer;
    private final BufferedReader reader;
    private final Gson json;

    public ClientNotifier(PrintWriter writer, BufferedReader reader, Gson json) {
        this.writer = writer;
        this.reader = reader;
        this.json = json;
    }

    /**
     * The entry point to instantiate a new instance of this class to send notifs on the given socket.
     * @param socket the socket to use between this server and the connected client.
     * @param json the Json (de)serializer to use
     * @param <T> the type of messages to use
     * @return a new instance of this class
     * @throws IOException if the socket is closed
     */
    public static <T> ClientNotifier<T> init(Socket socket, Gson json) throws IOException {

        return new ClientNotifier<>(
                new PrintWriter(socket.getOutputStream(), true),
                new BufferedReader(new InputStreamReader(socket.getInputStream())),
                json
        );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyNewChatroom(Chatroom<T> chatroom) {
        writer.println(
                0
                        + json.toJson(chatroom)
        );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyNewMessage(int chatroomId, Message<T> message) {
        writer.println(
                Integer.toString(1)
                        + chatroomId
                        + json.toJson(message)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyUserChange(UserInfo user) {
        writer.println(
                2
                        + json.toJson(user)
        );
    }

    /**
     * Checks if the client is still connected
     * @return false if the socket is not reachable
     */
    public boolean stillConnected() {
        try {
            return reader.read() > 0;
        } catch (IOException e) {
            return false;
        }
    }

}
