package chatProject.model.messages;

import chatProject.model.user.Status;
import chatProject.model.user.UserAccount;
import chatProject.model.user.UserInfo;

/**
 * Extends the UserInfo. Useful if there is a need for some specific code in the owner of a Message.
 */
public abstract class MessageOwnerAbstract extends UserInfo {

    // we need to match the UserInfo constructor (no other need here)
    public MessageOwnerAbstract(UserAccount account, Status currentStatus) {
        super(account, currentStatus);
    }
}
