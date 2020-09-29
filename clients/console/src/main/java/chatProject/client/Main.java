package chatProject.client;

import chatProject.client.chat.algo.ChatClient;
import chatProject.client.gui.text.MainGUI;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {

        if (args.length != 3) {
            System.err.println("Required arguments : <server hostname> <socket port (listener)> <web server port>");
            System.exit(1);
        }

        String serverHostname = args[0];
        int socketPort = Integer.parseInt(args[1]);
        int webServerPort = Integer.parseInt(args[2]);

        // create the client
        try (final ChatClient<String> client = ChatClient.initEmptyChat(serverHostname, webServerPort, socketPort)) {

            // init the UI
            MainGUI terminal = MainGUI.init(client);
            terminal.close();

        }
    }
}
