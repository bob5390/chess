package server;

import java.io.IOException;
import java.net.URI;

import com.google.gson.Gson;

import chess.ChessMove;
import jakarta.websocket.*;
import websocket.commands.ConnectCommand;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.ServerMessage;

public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageObserver listener;

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
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    listener.notify(notification);
                }
            });
        } catch(Exception e) {
            throw new Exception("Error: " + e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {}
    
    public void makeMove(ChessMove move) throws Exception {
        MoveCommand command = new MoveCommand(move);
        try {
            session.getBasicRemote().sendText(new Gson().toJson(command));
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
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    listener.notify(notification);
                }
            });

            ConnectCommand connectCommand = new ConnectCommand(authToken, gameID, color);
            session.getBasicRemote().sendText(new Gson().toJson(connectCommand));
        } catch(Exception e) {
            throw new Exception("Error: " + e.getMessage());
        }
    }
}
