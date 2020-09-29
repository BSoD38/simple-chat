package chatProject.algo;

import chatProject.model.listener.MessageListener;
import chatProject.model.messages.Message;
import chatProject.model.user.UserInfo;

import java.util.List;

/**
 * The algorithms to manage {@link Message}s.
 * @param <T> the type of messages in the chat
 */
public interface MessageAlgo<T> extends MessageListener<T> {

    /**
     * Adds a new {@link Message} in the model and notifies clients about it.
     * @param chatroomId the ID of the chatroom that contains the message
     * @param user the use who sent the message
     * @param content the content of the message
     * @return the new message added to the model
     */
    Message<T> addMessage(int chatroomId, UserInfo user, T content);

    /**
     * Gets the list of messages in a chatroom.
     * @param chatroomId the ID of the chatroom to query
     * @return the list of messages in this chatroom
     */
    List<Message<T>> getChatroomMessages(int chatroomId);
}
