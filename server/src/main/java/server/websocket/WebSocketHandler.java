package server.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import chess.ChessGame;
import chess.InvalidMoveException;
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
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

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
                if(toExclude == null || !s.equals(toExclude)) {
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
        NotificationMessage notification = new NotificationMessage(message);
        connections.get(command.getGameID()).add(session);
        notifySessions(command.getGameID(), session, new Gson().toJson(notification));
    }

    private void makeMove(Session session, String username, MoveCommand command) throws IOException, InvalidMoveException {
        GameData gameData = dbAccess.getGame(command.getGameID().toString());
        ChessGame game = gameData.getChessGame();
        if(!game.validMoves(command.getMove().getStartPosition()).contains(command.getMove())) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Move is invalid, cannot make move");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        } else {
            game.makeMove(command.getMove());
            gameData.setGame(game);
            dbAccess.updateGame(gameData);
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.getChessGame());
            notifySessions(command.getGameID(), null, new Gson().toJson(loadGameMessage));
            String message = String.format("Move was made from %s to %s", command.getMove().getStartPosition().toString(), command.getMove().getEndPosition().toString());
            NotificationMessage notification = new NotificationMessage(message);
            notifySessions(command.getGameID(), session, new Gson().toJson(notification));

            ChessGame.TeamColor currentTurn = game.getTeamTurn();
            if(game.isInCheckmate(currentTurn)) {
                message = String.format("%s is in checkmate", currentTurn.toString().toLowerCase());
            } else if(game.isInCheck(currentTurn)) {
                message = String.format("%s is in check", currentTurn.toString().toLowerCase());
            } else if(game.isInStalemate(currentTurn)) {
                message = String.format("%s is in stalemate", currentTurn.toString().toLowerCase());
            }
            if(!message.contains("Move")) {
                notification = new NotificationMessage(message);
                notifySessions(command.getGameID(), null, message);
            }
        }
    }

    private void leaveGame(Session session, String username, UserGameCommand command) {
        connections.get(command.getGameID()).remove(session);
    }

    private void resign(Session session, String username, UserGameCommand command) {
        connections.get(command.getGameID()).remove(session);
    }
}
