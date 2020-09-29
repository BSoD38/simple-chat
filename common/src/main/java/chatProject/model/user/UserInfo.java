package chatProject.model.user;

import java.util.Objects;

/**
 * A class to model a user in the chat.
 */
public class UserInfo {

    /**
     * The account of the user.
     */
    private final UserAccount account;
    /**
     * The current status of the user.
     */
    private Status currentStatus;

    public UserInfo(UserAccount account, Status currentStatus) {
        this.account = account;
        this.currentStatus = currentStatus;
    }

    /**
     * Gets the account holding information about the user.
     * @return the user account
     */
    public UserAccount getAccount() {
        return account;
    }

    /**
     * Gets the current status of the user.
     * @return the status of the user
     */
    public Status getCurrentStatus() {
        return currentStatus;
    }

    /**
     * Sets the current satus of the user.
     * @param currentStatus the status of the user
     */
    public void setCurrentStatus(Status currentStatus) {
    }

    @Override
    public String toString() {
        return getAccount().getUsername() + '(' + getCurrentStatus().getPrintableRepresentation() + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return account.equals(userInfo.account) &&
                currentStatus == userInfo.currentStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(account);
    }
}
