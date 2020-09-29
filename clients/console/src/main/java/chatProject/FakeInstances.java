package chatProject;

import chatProject.model.user.Status;
import chatProject.model.user.UserAccount;
import chatProject.model.messages.ChatInstance;
import chatProject.model.messages.Chatroom;
import chatProject.model.messages.Message;
import chatProject.model.user.UserInfo;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * This class contains fake instances to be used in the project or in tests.
 */
public class FakeInstances {

    public static final UserAccount UNKNOWN_USER = new UserAccount(-1, "?");
    public static final UserInfo UNKNOWN_USER_INFO = new UserInfo(UNKNOWN_USER, Status.REVOKED);

    public static final UserAccount DUMMY_ACCOUNT_1 = new UserAccount(3, "User1");
    public static final UserAccount DUMMY_ACCOUNT_2 = new UserAccount(7, "User2");

    public static final UserInfo DUMMY_ACTIVE_USER = new UserInfo(DUMMY_ACCOUNT_1, Status.ACTIVE);
    public static final UserInfo DUMMY_LOGOUT_USER = new UserInfo(DUMMY_ACCOUNT_2, Status.INACTIVE);

    public static final Map<UserInfo, LocalTime> DUMMY_USERS_MAP = new HashMap<>();
    static {
        DUMMY_USERS_MAP.put(DUMMY_ACTIVE_USER, LocalTime.now());
        DUMMY_USERS_MAP.put(DUMMY_LOGOUT_USER, LocalTime.now());
    }

    public static final Message<String> DUMMY_MESSAGE_1 =
            new Message<>(0, DUMMY_ACTIVE_USER, "Hello");
    public static final Message<String> DUMMY_MESSAGE_2 =
            new Message<>(1, DUMMY_LOGOUT_USER, "Bye");

    public static final Chatroom<String> DUMMY_CHATROOM_1 =
            new Chatroom<>(
                    "ROOM 1",
                    DUMMY_ACTIVE_USER,
                    new ArrayList<>(asList(DUMMY_MESSAGE_2))
            );
    public static final Chatroom<String> DUMMY_CHATROOM_2 =
            new Chatroom<>(
                    "ROOM 2",
                    DUMMY_LOGOUT_USER,
                    new ArrayList<>(asList(DUMMY_MESSAGE_1, DUMMY_MESSAGE_2))
            );

    public static final ChatInstance<String> DUMMY_CHAT_INSTANCE =
            new ChatInstance<>(
                    new ArrayList<>(asList(DUMMY_CHATROOM_1, DUMMY_CHATROOM_2)),
                    DUMMY_USERS_MAP
                    );

}
