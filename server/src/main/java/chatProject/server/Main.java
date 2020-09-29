package chatProject.server;

import com.google.gson.Gson;

import java.io.IOException;

/**
 * The main class for the server instance.
 */
public class Main {

    public static <T> void main(String... args) throws IOException {

        if (args.length != 2) {
            System.err.println("Required arguments : <socket port (listener)> <web server port>");
            System.exit(1);
        }

        // the port of the socket opened in the ChatServer instance to send notifications to clients
        int socketPort = Integer.parseInt(args[0]);
        // the port to expose the web services of the ChatServerService
        int webServerPort = Integer.parseInt(args[1]);

        // init the Chat algo
        final Gson json = new Gson();
        // start the server implementation
        final ChatServer<String> server = ChatServer.initEmptyChat(socketPort, json);

        /*
        new ChatServer<>(FakeInstances.DUMMY_CHAT_INSTANCE, new HashSet<>(), json);

        final Thread serverThread = new Thread(() -> {
            try {
                server.openSocket(socketPort);
            } catch (IOException e) {
                System.exit(1);
            }
        });
        serverThread.start();
         */

        // start the web services
        new ChatServerService<>(server, json).serve(webServerPort);

    }
}
