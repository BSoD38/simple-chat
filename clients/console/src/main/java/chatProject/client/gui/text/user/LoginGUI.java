package chatProject.client.gui.text.user;

import chatProject.FakeInstances;
import chatProject.client.chat.algo.ChatClient;
import chatProject.client.gui.text.helpers.GUIHelpers;
import chatProject.model.user.UserInfo;
import chatProject.client.gui.text.main.MainWindowGUI;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;

/**
 * The window for login a user.
 * @param <T> the type of messages to use
 */
public class LoginGUI<T> {
    private final ChatClient<T> chat;
    private final Window window;
    private final Panel contentPanel;
    private final TextBox userName = new TextBox(new TerminalSize(20, 1));
    private final Label errors = new Label("").setForegroundColor(TextColor.ANSI.RED);

    public LoginGUI(ChatClient<T> chat, Window window, Panel contentPanel) {
        this.chat = chat;
        this.window = window;
        this.contentPanel = contentPanel;
    }

    public static <T> void init(ChatClient<T> chat, WindowBasedTextGUI textGUI) {

        final Window window = new BasicWindow("Login");

        Panel contentPanel = new Panel(new GridLayout(1));
        GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
        gridLayout.setHorizontalSpacing(2);

        // init instance
        final LoginGUI<T> instance = new LoginGUI<>( chat, window, contentPanel);
        instance.createWindow();

        // render the window
        textGUI.addWindowAndWait(window);
    }

    private void createWindow() {

        GUIHelpers.addTitle("Login", contentPanel);

        contentPanel.addComponent(userName);

        contentPanel.addComponent(
                new Button("Login", () -> {
                    final UserInfo login = chat.login(userName.getText());
                    if (login != null && login != FakeInstances.UNKNOWN_USER_INFO) {
                        errors.setText("");
                        final WindowBasedTextGUI textGUI = window.getTextGUI();
                        window.close();
                        MainWindowGUI.init(chat, textGUI);
                    } else {
                        errors.setText("Invalid login");
                    }
                })
        );

        contentPanel.addComponent(errors);

        GUIHelpers.addCloseButton(contentPanel, window);

        window.setComponent(contentPanel);
    }

}
