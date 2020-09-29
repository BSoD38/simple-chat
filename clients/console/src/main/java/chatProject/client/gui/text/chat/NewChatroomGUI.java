package chatProject.client.gui.text.chat;

import chatProject.client.chat.algo.ChatClient;
import chatProject.client.gui.text.helpers.GUIHelpers;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

/**
 * The window to create a new {@link chatProject.model.messages.Chatroom}
 * @param <T> the type of messages to use
 */
public class NewChatroomGUI<T> {

    private final ChatClient<T> chat;
    private final Window window;
    private final Panel contentPanel;
    private final TextBox chatroomName = new TextBox(new TerminalSize(20, 1));

    public NewChatroomGUI(ChatClient<T> chat, Window window, Panel contentPanel) {
        this.chat = chat;
        this.window = window;
        this.contentPanel = contentPanel;
    }

    public static <T> void init(ChatClient<T> chat, WindowBasedTextGUI textGUI) {

        final Window window = new BasicWindow("New Chatroom");

        Panel contentPanel = new Panel(new GridLayout(1));
        GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
        gridLayout.setHorizontalSpacing(2);

        // init instance
        final NewChatroomGUI<T> instance = new NewChatroomGUI<>( chat, window, contentPanel);
        instance.createWindow();

        // render the window
        textGUI.addWindowAndWait(window);
    }

    private void createWindow() {

        GUIHelpers.addTitle("New Chatroom", contentPanel);

        contentPanel.addComponent(chatroomName);

        contentPanel.addComponent(
                new Button("Create", () -> {
                    chat.createChatroomFromCurrentUser(chatroomName.getText());
                    window.close();
                })
        );

        GUIHelpers.addCloseButton(contentPanel, window);

        window.setComponent(contentPanel);
    }

}
