package chatProject.client.gui.text.main;

import chatProject.client.chat.algo.ChatClient;
import chatProject.client.gui.text.chat.ChatroomGUI;
import chatProject.client.gui.text.chat.NewChatroomGUI;
import chatProject.client.gui.text.helpers.GUIHelpers;
import chatProject.model.listener.ChatroomsListener;
import chatProject.model.messages.Chatroom;
import chatProject.model.user.UserAccount;
import com.googlecode.lanterna.gui2.*;

/**
 * The main window, after a login.
 * @param <T> the type of messages to use
 */
public class MainWindowGUI<T> implements ChatroomsListener<T> {

    private final ChatClient<T> chat;
    private final Window window;
    private final Panel contentPanel;
    private final ComboBox<String> chatroomComboBox;

    public MainWindowGUI(
            ChatClient<T> chat,
            Window window,
            Panel contentPanel,
            ComboBox<String> chatroomComboBox) {
        this.chat = chat;
        this.window = window;
        this.contentPanel = contentPanel;
        this.chatroomComboBox = chatroomComboBox;
    }

    public static <T> void init(ChatClient<T> chat, WindowBasedTextGUI textGUI) {

        final Window window = new BasicWindow("Simple Chat");

        Panel contentPanel = new Panel(new GridLayout(2));
        GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
        gridLayout.setHorizontalSpacing(2);

        final MainWindowGUI<T> instance = new MainWindowGUI<>(
                chat,
                window,
                contentPanel,
                new ComboBox<>(chat.getCurrentChatroomNames())
                );
        instance.createWindow();

        chat.addChatroomListener(instance);

        textGUI.addWindowAndWait(window);
    }

    private void createWindow() {

        addCurrentUser();

        GUIHelpers.addTitle("Chatroom :", contentPanel);
        chatroomCombo();
        addNewChatroomWindow();

        GUIHelpers.addCloseButton(contentPanel, window);

        window.setComponent(contentPanel);
    }

    private void addNewChatroomWindow() {
        contentPanel.addComponent(
            new Button("New Chatroom", () -> NewChatroomGUI.init(chat, window.getTextGUI()))
        );
    }

    private void addCurrentUser() {
        final UserAccount currentUser = chat.getCurrentUser().getAccount();
        final String username = (currentUser == null) ? "?" : currentUser.getUsername();
        contentPanel.addComponent(
        new Label("Current user : " + username)
        );
        GUIHelpers.addHorizontalSeparator(contentPanel);
    }

    private void chatroomCombo() {

        chatroomComboBox.setReadOnly(true);
        chatroomComboBox.addListener(
                (chatroomId, previous) -> ChatroomGUI.init(chat, chatroomId, window.getTextGUI())
        );
        contentPanel.addComponent(chatroomComboBox);
    }

    @Override
    public Chatroom<T> notifyNewChatroom(Chatroom<T> newChatroom) {
        chatroomComboBox.addItem(newChatroom.getName());
        return newChatroom;
    }
}
