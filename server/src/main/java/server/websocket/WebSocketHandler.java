package server.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import dataaccess.DataAccess;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import websocket.commands.ConnectCommand;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    HashMap<Integer, Set<Session>> connections = new HashMap<Integer, Set<Session>>();
    DataAccess dbAccess;

    public WebSocketHandler(DataAccess dataAccess) {
        this.dbAccess = dataAccess;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        Session session = ctx.session;

        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            String authToken = command.getAuthToken();
            String username = dbAccess.getAuth(authToken).getUsername();

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand)command);
                case MAKE_MOVE -> makeMove(session, username, new Gson().fromJson(ctx.message(), MoveCommand.class));
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(session, username, command);
            }
        } catch(UnauthorizedResponse ex) { // TODO: finish this error handling
            ex.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void notifySessions(Integer gameID, Session toExclude, String message) throws IOException {
        for(Session s : connections.get(gameID)) {
            if(s.isOpen()) {
                if(!s.equals(toExclude)) {
                    s.getRemote().sendString(message);
                }
            }
        }
    }

    // TODO: finish these functions
    private void connect(Session session, String username, ConnectCommand command) throws IOException {
        String message = "";
        if(command.getColor().equals("observer")) {
            message = String.format("%s joined the game as an observer", username);
        } else {
            message = String.format("%s joined the game as %s", username, command.getColor());
        }
        connections.get(command.getGameID()).add(session);
        notifySessions(command.getGameID(), session, message);
    }

    private void makeMove(Session session, String username, MoveCommand command) {
        GameData gameData = dbAccess.getGame(command.getGameID().toString());
        // TODO: finish this method
    }

    private void leaveGame(Session session, String username, UserGameCommand command) {
        connections.get(command.getGameID()).remove(session);
    }

    private void resign(Session session, String username, UserGameCommand command) {
        connections.get(command.getGameID()).remove(session);
    }
}
