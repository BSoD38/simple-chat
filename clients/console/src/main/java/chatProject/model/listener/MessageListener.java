package chatProject.model.listener;

import chatProject.model.messages.Message;

/**
 * A listener on new {@link Message}s added in the chat.
 * @param <T> the type of messages in the chat
 */
public interface MessageListener<T> {

    /**
     * Notifies for a new message created
     * @param chatroomId the ID of the chatroom that received the message
     * @param message the new message
     * @return the new message
     */
    Message<T> notifyNewMessage(int chatroomId, Message<T> message);
}
