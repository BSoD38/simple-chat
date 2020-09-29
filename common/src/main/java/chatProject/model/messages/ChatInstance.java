package chatProject.model.messages;

import chatProject.model.user.UserAccount;
import chatProject.model.user.UserInfo;

import java.time.LocalTime;
import java.util.*;

/**
 * The main class for the model of the chat.
 * Holds a list of {@link Chatroom} that each contain many {@link Message}.
 * Also holds the list of {@link UserInfo}.
 * @param <T> the type of messages in the chat
 */
public class ChatInstance<T> {

    /**
     * The list of chatrooms in the chat.
     */
    private final List<Chatroom<T>> chatrooms;

    /**
     * The list of users in the chat.
     * The key is the user infomation
     * The value is the last login time
     */
    private final Map<UserInfo, LocalTime> users;

    public ChatInstance(List<Chatroom<T>> chatrooms, Map<UserInfo, LocalTime> users) {
        this.chatrooms = chatrooms;
        this.users = users;
    }

    /**
     * Gets the list of chatrooms in the chat.
     * @return the list of {@link Chatroom} in the model
     */
    public List<Chatroom<T>> getCurentChatrooms() {
        // return a safe read-only copy
        return Collections.unmodifiableList(chatrooms);
    }

    /**
     * Adds a new {@link Chatroom} in the chat.
     * @param newChatroom the chatroom to add
     * @return the ID of the new chatroom added
     */
    public int addChatroom(Chatroom<T> newChatroom) {
        this.chatrooms.add(newChatroom);
        return this.chatrooms.indexOf(newChatroom);
    }


    /**
     * Adds a new {@link UserInfo} in the chat.
     * @param newUser the user to add
     */
    public boolean addUser(UserInfo newUser) {
        if (users.get(newUser) != null) {
            // already found in the model (same account and same status) - no update
            return false;
        }

        // may be already in the model but needs only to update the account
        final UserAccount newUserAccount = newUser.getAccount();
        final Optional<UserInfo> accountAlreadyPresent = users.keySet()
                .stream()
                .filter(user -> user.getAccount().equals(newUserAccount))
                .findAny();
        if (accountAlreadyPresent.isPresent()) {
            final UserInfo userInfo = accountAlreadyPresent.get();
            userInfo.setCurrentStatus(newUser.getCurrentStatus());
            users.replace(userInfo, LocalTime.now());
            return true;
        } else {
            users.put(newUser, LocalTime.now());
            return true;
        }
    }

    /**
     * Gets the list of all registered users.
     * @return the collection of users in the chat.
     */
    public Map<UserInfo, LocalTime> getUsers() {
        return null;
    }

    /**
     * Creates a new chat with no {@link Chatroom} no {@link Message} and no {@link UserInfo}.
     * @param <T> the type of messages in the chat
     * @return the new chat instance
     */
    public static <T> ChatInstance<T> initEmptyChat() {
        return new ChatInstance<T>(null, null);
    }

}
