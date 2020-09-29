package chatProject.client.gui.text;

import chatProject.client.chat.algo.ChatClient;
import chatProject.client.gui.text.user.LoginGUI;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * The entry point to generate the client UI.
 */
public class MainGUI implements Closeable {

    private final Screen screen;

    public MainGUI(Screen screen) {
        this.screen = screen;
    }

    public static <T> MainGUI init(ChatClient<T> chat) throws IOException {

        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

        Screen screen = defaultTerminalFactory.createScreen();
        screen.startScreen();

        final MultiWindowTextGUI textGUI = new MultiWindowTextGUI(screen);
        LoginGUI.init(chat, textGUI);

        return new MainGUI(screen);
    }

    @Override
    public void close() throws IOException {
        this.screen.stopScreen();
    }

}
