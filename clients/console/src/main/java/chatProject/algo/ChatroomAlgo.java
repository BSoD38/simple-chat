package chatProject.algo;

import chatProject.model.listener.ChatroomsListener;
import chatProject.model.messages.Chatroom;
import chatProject.model.user.UserInfo;

import java.util.List;

/**
 * The algorithm to manage {@link Chatroom}s.
 * @param <T> the type of messages in the chat
 */
public interface ChatroomAlgo<T> extends ChatroomsListener<T> {

    /**
     * Gets the list of all {@link Chatroom}s.
     * @return the names of all chatrooms in the model
     */
    List<String> getCurrentChatroomNames();

    /**
     * Gets the model of a chatroom given its ID.
     * @param chatroomId the chatroom ID
     * @return the model of the chatroom
     */
    Chatroom<T> getChatroom(int chatroomId);

    /**
     * Adds a new {@link Chatroom} in the model and notifies clients about it.
     * @param chatroomName the name of the chatroom to create
     * @param owner the user who created the chatroom
     * @return the new chatroom ID
     */
    int addChatroom(String chatroomName, UserInfo owner);

}
