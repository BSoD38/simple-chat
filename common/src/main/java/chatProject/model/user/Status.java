package chatProject.model.user;

/**
 * The status of a user.
 */
public enum Status {

    ACTIVE("active"), // user logged in
    INACTIVE("idle"), // user idle or logged out
    REVOKED("revoked"); // this user cannot log in anymore

    /**
     * The printable representation of the status.
     */
    private final String printable;
    Status(String printable) {
        this.printable = printable;
    }

    public String getPrintableRepresentation() {
        return this.printable;
    }
}
