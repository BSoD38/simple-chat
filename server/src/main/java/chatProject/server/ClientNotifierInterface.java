package chatProject.server;

import chatProject.model.messages.Chatroom;
import chatProject.model.messages.Message;
import chatProject.model.user.UserInfo;

public interface ClientNotifierInterface<T> {

    /**
     * Sends a notification on the socket about a new chatroom.
     * The code of the notification is : 0
     * @param chatroom the chatroom to notify
     */
    void notifyNewChatroom(Chatroom<T> chatroom);

    /**
     * Sends a notification on the socket about a new message sent in a chatroom.
     * The code of the notification is : 1
     * @param chatroomId the ID of the chatroom
     * @param message the new message sent
     */
    void notifyNewMessage(int chatroomId, Message<T> message);

    /**
     * Sends a notification on the socket about a user changing its status or account information.
     * The code of the notification is : 2
     * @param user the new user to use
     */
    void notifyUserChange(UserInfo user);
}
