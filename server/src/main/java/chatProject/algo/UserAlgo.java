package chatProject.algo;

import chatProject.model.listener.UserListener;
import chatProject.model.user.UserInfo;

import java.util.Collection;

/**
 * The algorithms to manage {@link UserInfo}s.
 */
public interface UserAlgo extends UserListener {

    /**
     * Logs in the given user.
     * @param userName the username to login
     * @return the logged in user, if any
     */
    UserInfo login(String userName);

    /**
     * Gets all users registered in the chat.
     * @return the users stored in the chat.
     */
    Collection<UserInfo> getUsers();
}
