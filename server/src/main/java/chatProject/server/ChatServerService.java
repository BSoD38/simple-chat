package chatProject.server;

import chatProject.AddMessageForm;
import chatProject.model.user.UserInfo;
import com.google.gson.Gson;

import static spark.Spark.*;

/**
 * A class that exposes the Web services of the server.
 * Based on the Spark Java library.
 * @param <T> the type of messages to use (probably String)
 */
public class ChatServerService<T> {

    /**
     * The server instance to complete the real queries.
     */
    private final ChatServer<T> server;

    /**
     * A Json (de)serializer.
     */
    private final Gson json;

    public ChatServerService(ChatServer<T> server, Gson json) {
        this.server = server;
        this.json = json;
    }

    @SuppressWarnings("unchecked")
    public void serve(int webServerPort) {

        port(webServerPort);

        get("/chatrooms", (request, response) ->
                json.toJson(
                        server.getCurrentChatroomNames()
                )
        );

        get("/chatroom/:chatroomId", (request, response) ->
                json.toJson(
                        server.getChatroom(Integer.parseInt(request.params("chatroomId")))
                )
        );

        put("/chatroom/:chatroomName", (request, response) ->
            json.toJson(
                    server.addChatroom(
                            request.params("chatroomName"),
                            json.fromJson(request.body(), UserInfo.class)
                    )
            )
        );

        get("/messages/:chatroomId", (request, response) -> {
                    return json.toJson(
                            server.getChatroomMessages(Integer.parseInt(request.params("chatroomId")))
                    );
                }
        );

        post("/login", (request, response) ->
                json.toJson(
                        server.login(request.body())
                )
        );

        put("/message", (request, response) -> {
            final AddMessageForm<T> form = json.fromJson(request.body(), AddMessageForm.class);
            return json.toJson(
                    server.addMessage(form.getChatroomId(), form.getUser(), form.getContent())
            );
        });

        get("/users", (request, response) ->
                json.toJson(
                        server.getUsers()
                )
        );
    }

}