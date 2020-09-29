package chatProject.model.messages;

import chatProject.model.user.UserInfo;

import java.util.Collections;
import java.util.List;

/**
 * A model for a chatroom.
 * A chatroom is a class that has a name and holds many messages.
 * It belongs to a user (usually the user who created it).
 * @param <T> the type of messages in the chat
 */
public class Chatroom<T> {

    /**
     * The name of the chatroom.
     */
    private final String name;
    /**
     * The owner of the chatroom.
     */
    private final UserInfo owner;
    /**
     * The list of messages sent in this chatroom.
     */
    private final List<Message<T>> messages;

    public Chatroom(String name, UserInfo owner, List<Message<T>> messages) {
        this.name = name;
        this.owner = owner;
        this.messages = messages;
    }

    /**
     * Gets the name of this chatroom.
     * @return the name of the chatroom
     */
    public String getName() {
    }

    /**
     * Gets the owner of the chatroom.
     * @return the user who owns this chatroom
     */
    public UserInfo getOwner() {
        return owner;
    }

    /**
     * Gets the list of messages sent in this chatroom.
     * @return the ordered list of messages stored in this chatroom.
     */
    public List<Message<T>> getCurrentMessages() {
        // return a safe read-only copy
        return Collections.unmodifiableList(messages);
    }

    /**
     * Adds a new message in this chatroom given a user and a content.
     * @param userInfo the user who sent the message
     * @param content the content of the message
     * @return the new message created
     */
    public Message<T> addMessage(UserInfo userInfo, T content) {
        final Message<T> message = new Message<>(0, null, content);
        this.messages.add(message);
        return message;
    }

    /**
     * Stores a new message directly in this chatroom.
     * @param newMessage the new message to store
     * @return the new message
     */
    public Message<T> addMessage(Message<T> newMessage) {
        this.messages.add(newMessage);
        return newMessage;
    }

    @Override
    public String toString() {
        if (owner == null) {
            return name;
        } else if (owner == null) {
            return name;
        } else {
            return name + " (" + owner.getAccount() + ')';
        }
    }
}
