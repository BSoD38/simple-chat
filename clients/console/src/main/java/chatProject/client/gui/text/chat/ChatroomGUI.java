package chatProject.client.gui.text.chat;

import chatProject.client.chat.algo.ChatClient;
import chatProject.client.gui.text.helpers.GUIHelpers;
import chatProject.model.listener.MessageListener;
import chatProject.model.listener.UserListener;
import chatProject.model.messages.Message;
import chatProject.model.user.UserInfo;
import chatProject.model.user.Status;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

/**
 * The window of a {@link chatProject.model.messages.Chatroom}
 * @param <T> the type of messages to use
 */
public class ChatroomGUI<T> implements MessageListener<T>, UserListener {

    private final ChatClient<T> chat;
    private final int chatroomId;
    private final Window window;
    private final Panel contentPanel;
    private final TextBox messages;

    private Thread msgUpdateThread = null;

    public ChatroomGUI(ChatClient<T> chat, int chatroomId, Window window, Panel contentPanel, TextBox messages) {
        this.chat = chat;
        this.chatroomId = chatroomId;
        this.window = window;
        this.contentPanel = contentPanel;
        this.messages = messages;
    }

    public static <T> void init(
            ChatClient<T> chat,
            int chatroomId,
            WindowBasedTextGUI textGUI) {

        final Window window = new BasicWindow("Chatroom");

        Panel contentPanel = new Panel(new GridLayout(1));
        GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
        gridLayout.setHorizontalSpacing(2);

        // init instance
        final TextBox messagesTextBox = new TextBox(new TerminalSize(60, 10))
                .setReadOnly(true)
                .setEnabled(false);
        final ChatroomGUI<T> instance = new ChatroomGUI<>(
                chat,
                chatroomId,
                window,
                contentPanel,
                messagesTextBox
        );
        instance.createWindow();

        // listen on new messages
        chat.addMessageListener(chatroomId, instance);
        chat.addUserListener(instance);

        instance.msgUpdateThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000); // update every 5s
                    instance.fillMessagesContent();
                } catch (InterruptedException e) {
                    // interrupted
                    return;
                }
            }
        });
        instance.msgUpdateThread.start();

        // render the window
        textGUI.addWindowAndWait(window);
    }

    public void createWindow() {
        GUIHelpers.addTitle(chat.getChatroomName(chatroomId), contentPanel);
        messagesComponent();
        addNewMessageBox();
        addCloseButton();

        window.setComponent(contentPanel);
    }

    private void addCloseButton() {

        final Runnable action =  () -> {
            window.close();
            msgUpdateThread.interrupt();
        };

        // add an horizontal space
        contentPanel.addComponent(
                new EmptySpace()
                        .setLayoutData(
                                GridLayout.createHorizontallyFilledLayoutData(1)));

        // add an horizontal separator
        contentPanel.addComponent(
                new Separator(Direction.HORIZONTAL)
                        .setLayoutData(
                                GridLayout.createHorizontallyFilledLayoutData(1)));

        // add the button with the action
        contentPanel.addComponent(
                new Button("Close", action).setLayoutData(
                        GridLayout.createHorizontallyEndAlignedLayoutData(1)));

        // the button is added

        contentPanel.getChildCount();
        contentPanel.getChildCount();
    }


    private void messagesComponent() {
        fillMessagesContent();
        contentPanel.addComponent(messages);
    }

    private void addNewMessageBox() {
        GUIHelpers.addHorizontalSeparator(contentPanel);
        final TextBox messageContent = new TextBox();
        contentPanel.addComponent(messageContent);
        contentPanel.addComponent(
                new Button("Send", sendButtonAction(chatroomId, messageContent))
        );

    }

    @SuppressWarnings("unchecked")
    private Runnable sendButtonAction(int chatroomId, TextBox message) {
        return () -> {
            chat.sendMessageForCurrentUser(chatroomId, (T) message.getText());
            message.setText("");
        };
    }

    private void fillMessagesContent() {
        messages.setText("");
        chat
                .getChatroomMessages(chatroomId)
                .forEach(msg -> messages.addLine(messageAsString(msg)));
    }

    private String messageAsString(Message<T> msg) {
        final UserInfo sender = msg.getSender();
        return GUIHelpers.getUserRepresentation(
                // Force user status refresh from the user model if the user is not in the model anymore
                chat.getUsers().stream().filter(userInfo -> sender.getAccount().equals(userInfo.getAccount()))
                        .findAny().orElseGet(() -> {
                        sender.setCurrentStatus(Status.INACTIVE);
                        return sender;
                })
        ) + "> " + msg.getMessage().toString();
    }


    @Override
    public Message<T> notifyNewMessage(int chatroomId, Message<T> message) {
        messages.addLine(messageAsString(message));
        return message;
    }

    @Override
    public UserInfo notifyUserChange(UserInfo user) {
        // a user changed - refresh the messages (best effort)
        // the content is already refreshed by the refresh thread every second
        return user;
    }
}
