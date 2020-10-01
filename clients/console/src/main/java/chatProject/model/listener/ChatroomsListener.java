package chatProject.model.listener;


import chatProject.model.messages.Chatroom;

/**
 * A listener on new {@link Chatroom}s added in the chat.
 * @param <T> the type of messages in the chat
 */
public interface ChatroomsListener<T> {

    /**
     * Notifies clients about a new chatroom.
     * @param newChatroom the new chatroom
     */
    void notifyNewChatroom(Chatroom<T> newChatroom);
}
