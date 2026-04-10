package server;

import java.io.IOException;
import java.net.URI;

import com.google.gson.Gson;

import chess.ChessMove;
import jakarta.websocket.*;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;

public class WebSocketFacade extends Endpoint {
    private Session session;
    ServerMessageObserver listener;
    private Gson gson = new Gson();

    public WebSocketFacade() {
        this.session = null;
        this.listener = null;
    }

    public WebSocketFacade(String url, ServerMessageObserver listener) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.listener = listener;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    listener.notify(message);
                }
            });
        } catch(Exception e) {
            throw new Exception((e.getMessage().toLowerCase().startsWith("error")? "":"Error: ") + e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {}
    
    public void makeMove(String authToken, Integer gameID, ChessMove move) throws Exception {
        MoveCommand command = new MoveCommand(authToken, gameID, move);
        try {
            session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Error: " + e.getMessage());
        }
    }

    public void connect(String url, ServerMessageObserver listener, String authToken, String color, Integer gameID) throws Exception {
        if(session != null) {
            session.close();
        }
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.listener = listener;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, socketURI);

            //set message handler
            session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    listener.notify(message);
                }
            });

            UserGameCommand connectCommand = new UserGameCommand(CommandType.CONNECT, authToken, gameID);
            session.getBasicRemote().sendText(gson.toJson(connectCommand));
        } catch(Exception e) {
            throw new Exception("Error: " + e.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void leave(String authToken, Integer gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        session.getBasicRemote().sendText(gson.toJson(command));
    }
}
