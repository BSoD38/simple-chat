package specifications

import chatProject.model.messages.ChatInstance
import chatProject.model.messages.Chatroom
import chatProject.server.ChatServer
import chatProject.server.ClientNotifierInterface
import spock.lang.Specification

class ChatServerChatroomSpec extends Specification {

    def "Adding a new Chatroom should add it in the model"() {
        given: "A server"
        def server = new ChatServer(ChatInstance.initEmptyChat(), null, null)

        when: "A new chatroom is created"
        server.addChatroom("Test chatroom", null)

        then: "The new chatroom should be added to the model"
        server.currentChatroomNames.contains("Test chatroom")
    }

    def "Adding a new Chatroom should notify clients about the new chatroom"() {
        given: "A client notifier"
        // use a Mock (fake) ClientNotifier to avoid opening a real socket
        // we only want to test interactions
        def clientNotifier = Mock(ClientNotifierInterface)
        and: "A server with the client notifier"
        def server = new ChatServer(ChatInstance.initEmptyChat(), [clientNotifier], null)

        when: "A new chatroom is created"
        server.addChatroom("Test chatroom", null)

        then: "The client listener should be notified about a new chatroom"
        // this check means : the 'clientNotifier.notifyNewChatroom()' method was called 1x
        1 * clientNotifier.notifyNewChatroom(_)
    }

    def "The server should retrieve a Chatroom model from its ID"() {
        given: "A server with a Chatroom"
        // a chatroom
        def chatroom = new Chatroom("Test chatroom", null, null)
        // create a server with the chatroom in the model
        def server = new ChatServer(new ChatInstance([chatroom], null), null, null)

        expect: "The chatroom model should be retrieved from its ID"
        // there is only 1 chatroom in the model - get it
        server.getChatroom(0) == chatroom
    }

}
