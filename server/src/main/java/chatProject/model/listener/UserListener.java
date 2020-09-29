package chatProject.model.listener;

import chatProject.model.user.UserInfo;

/**
 * A listener on {@link UserInfo} changes.
 */
public interface UserListener {

    /**
     * Notifies for a user change (status and/or account).
     * @param user the user that changed.
     * @return the new user
     */
    UserInfo notifyUserChange(UserInfo user);
}
