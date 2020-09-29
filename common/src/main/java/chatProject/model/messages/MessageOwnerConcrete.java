package chatProject.model.messages;

import chatProject.model.user.Status;
import chatProject.model.user.UserAccount;

/**
 * The message owner.
 * Extends the MessageOwnerAbstract only to have a concrete class
 * May be removed if no more MessageOwnerAbstract
 */
public class MessageOwnerConcrete extends MessageOwnerAbstract {

    public MessageOwnerConcrete(UserAccount account, Status currentStatus) {
        super(account, currentStatus);
    }
}
