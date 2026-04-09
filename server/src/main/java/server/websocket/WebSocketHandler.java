package server.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import chess.ChessGame;
import chess.InvalidMoveException;
import dataaccess.DataAccess;
import io.javalin.http.HttpResponseException;
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
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private HashMap<Integer, Set<Session>> connections = new HashMap<Integer, Set<Session>>();
    private DataAccess dbAccess;
    private Gson gson = new Gson();

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
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
            String authToken = command.getAuthToken();
            String username = dbAccess.getAuth(authToken).getUsername();

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand)command);
                case MAKE_MOVE -> makeMove(session, username, gson.fromJson(ctx.message(), MoveCommand.class));
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(session, username, command);
            }
        } catch(UnauthorizedResponse ex) { // TODO: finish this error handling
            ex.printStackTrace();
        } catch(HttpResponseException e) {
            e.printStackTrace();
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

    private void connect(Session session, String username, ConnectCommand command) throws IOException {
        String message = "";
        if(command.getColor().equals("observer")) {
            message = String.format("%s joined the game as an observer", username);
        } else {
            message = String.format("%s joined the game as %s", username, command.getColor());
        }
        NotificationMessage notification = new NotificationMessage(message);
        connections.get(command.getGameID()).add(session);
        notifySessions(command.getGameID(), session, gson.toJson(notification));
    }

    private void makeMove(Session session, String username, MoveCommand command) throws IOException, InvalidMoveException, HttpResponseException {
        GameData gameData = dbAccess.getGame(command.getGameID().toString());
        ChessGame game = gameData.getChessGame();
        if(!game.validMoves(command.getMove().getStartPosition()).contains(command.getMove())) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Move is invalid, cannot make move");
            session.getRemote().sendString(gson.toJson(errorMessage));
        } else {
            game.makeMove(command.getMove());
            gameData.setGame(game);
            dbAccess.updateGame(gameData);
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.getChessGame());
            notifySessions(command.getGameID(), null, gson.toJson(loadGameMessage));
            String message = String.format("Move was made from %s to %s", command.getMove().getStartPosition().toString(), command.getMove().getEndPosition().toString());
            NotificationMessage notification = new NotificationMessage(message);
            notifySessions(command.getGameID(), session, gson.toJson(notification));

            ChessGame.TeamColor currentTurn = game.getTeamTurn();
            String currentPlayer = (gameData.getWhiteUsername().equals(username))? gameData.getBlackUsername():gameData.getWhiteUsername();
            if(game.isInCheckmate(currentTurn)) { // TODO: may have to end the game somehow here
                message = String.format("%s is in checkmate", currentPlayer);
                game.setGameOver(true);
                gameData.setGame(game);
                dbAccess.updateGame(gameData);
            } else if(game.isInCheck(currentTurn)) {
                message = String.format("%s is in check", currentPlayer);
            } else if(game.isInStalemate(currentTurn)) {
                message = String.format("%s is in stalemate", currentPlayer);
                game.setGameOver(true);
                gameData.setGame(game);
                dbAccess.updateGame(gameData);
            }
            if(!message.contains("Move")) {
                notification = new NotificationMessage(message);
                notifySessions(command.getGameID(), null, message);
            }
        }
    }

    private void leaveGame(Session session, String username, UserGameCommand command) throws IOException, HttpResponseException {
        GameData gameData = dbAccess.getGame(command.getGameID().toString());
        if(gameData.getBlackUsername().equals(username)) {
            gameData.setBlackUsername(null);
        } else if(gameData.getWhiteUsername().equals(username)) {
            gameData.setWhiteUsername(null);
        } // otherwise it is an observer - no game data update to do
        dbAccess.updateGame(gameData);
        String message = String.format("%s left the game.", username);
        NotificationMessage notification = new NotificationMessage(message);
        notifySessions(command.getGameID(), session, gson.toJson(notification));
        connections.get(command.getGameID()).remove(session);
        session.close();
    }

    private void resign(Session session, String username, UserGameCommand command) throws IOException {
        GameData gameData = dbAccess.getGame(command.getGameID().toString());
        ChessGame game = gameData.getChessGame();
        game.setGameOver(true);
        gameData.setGame(game);
        dbAccess.updateGame(gameData);
        String message = String.format("%s resigned.", username);
        NotificationMessage notification = new NotificationMessage(message);
        notifySessions(command.getGameID(), null, gson.toJson(notification));
    }
}
