package specifications

import chatProject.model.messages.ChatInstance
import chatProject.model.messages.Message
import chatProject.server.ChatServer
import chatProject.server.ClientNotifierInterface
import spock.lang.Specification

class ChatServerMessageSpec extends Specification {

    def "Adding a new Message should add it in the model"() {
        given: "A server"
        def server = new ChatServer(ChatInstance.initEmptyChat(), null, null)

        and: "A chatroom"
        int chatroomId = server.addChatroom(null, null)

        when: "A new message is created in the Chatroom"
        server.addMessage(chatroomId, null, "Test message")

        then: "The new message should be added to the model"
        // get messages in the chatroom
        server.getChatroomMessages(chatroomId)
                // collect only the message content from the Message class
                .collect { it.message }
                // check if the content matches the expected one
                .contains("Test message")
    }

    def "Adding a new Message should notify clients about the new message"() {
        given: "A client notifier"
        // use a Mock (fake) ClientNotifier to avoid opening a real socket
        // we only want to test interactions
        def clientNotifier = Mock(ClientNotifierInterface)
        and: "A server with the client notifier"
        def server = new ChatServer(ChatInstance.initEmptyChat(), [clientNotifier], null)

        and: "A chatroom"
        int chatroomId = server.addChatroom(null, null)

        when: "A new message is added"
        server.addMessage(chatroomId, null, "Test message")

        then: "The client listener should be notified about a new message"
        // this check means : the 'clientNotifier.notifyNewMessage()' method was called 1x
        // on the expected chatroom
        1 * clientNotifier.notifyNewMessage(chatroomId, _)
    }

}
