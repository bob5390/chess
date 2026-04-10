package server.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import chess.ChessGame;
import dataaccess.DataAccess;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
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
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, gson.fromJson(ctx.message(), MoveCommand.class));
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(session, username, command);
            }
        } catch(Exception e) {
            e.printStackTrace();
            ErrorMessage errorMessage = new ErrorMessage((e.getMessage().toLowerCase().startsWith("error")? "":"Error: ") + e.getMessage());
            try {
                session.getRemote().sendString(gson.toJson(errorMessage));
            } catch (IOException e1) {
                e1.printStackTrace(); // can't do much more with this error
            }
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

    private void connect(Session session, String username, UserGameCommand command) throws Exception {
        GameData gameData = dbAccess.getGame(command.getGameID().toString());
        if(gameData == null) {
            throw new Exception("Error: Couldn't find game data. Is the game ID correct?");
        }
        
        ChessGame game = gameData.getChessGame();
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        session.getRemote().sendString(gson.toJson(loadGameMessage));

        String message = "";
        if(gameData.getBlackUsername() != null && gameData.getBlackUsername().equals(username)) {
            message = String.format("%s joined the game as black", username);
        } else if(gameData.getWhiteUsername() != null && gameData.getWhiteUsername().equals(username)) {
            message = String.format("%s joined the game as white", username);
        } else {
            message = String.format("%s joined the game as an observer", username);
        }

        NotificationMessage notification = new NotificationMessage(message);
        if(connections.containsKey(command.getGameID())) {
            connections.get(command.getGameID()).add(session);
        } else {
            Set<Session> toAdd = new HashSet<Session>();
            toAdd.add(session);
            connections.put(command.getGameID(), toAdd);
        }
        notifySessions(command.getGameID(), session, gson.toJson(notification));
    }

    private void makeMove(Session session, String username, MoveCommand command) throws Exception {
        GameData gameData = dbAccess.getGame(command.getGameID().toString());
        if(gameData == null) {
            throw new Exception("Error: Couldn't find game data. Is the game ID correct?");
        }
        ChessGame game = gameData.getChessGame();
        if(game.getTeamTurn().toString().equals("WHITE")) {
            if(!gameData.getWhiteUsername().equals(username)) {
                throw new Exception("Error: It's not your turn!");
            }
        } else {
            if(!gameData.getBlackUsername().equals(username)) {
                throw new Exception("Error: It's not your turn!");
            }
        }
        if(!game.validMoves(command.getMove().getStartPosition()).contains(command.getMove())) {
            throw new Exception("Error: Move is invalid, cannot make move");
        } else {
            game.makeMove(command.getMove());
            gameData.setGame(game);
            gameData = dbAccess.updateGame(gameData); // updating might not be working right now
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.getChessGame());
            notifySessions(command.getGameID(), null, gson.toJson(loadGameMessage));
            String message = String.format("%s moved from %s to %s", username, command.getMove().getStartPosition().toString(), command.getMove().getEndPosition().toString());
            NotificationMessage notification = new NotificationMessage(message);
            notifySessions(command.getGameID(), session, gson.toJson(notification));

            ChessGame.TeamColor currentTurn = game.getTeamTurn();
            String currentPlayer = (gameData.getWhiteUsername().equals(username))? gameData.getBlackUsername():gameData.getWhiteUsername();
            if(game.isInCheckmate(currentTurn)) {
                message = String.format("%s is in checkmate", currentPlayer);
                game.setGameOver(true);
                gameData.setGame(game);
                gameData = dbAccess.updateGame(gameData);
            } else if(game.isInCheck(currentTurn)) {
                message = String.format("%s is in check", currentPlayer);
            } else if(game.isInStalemate(currentTurn)) {
                message = String.format("%s is in stalemate", currentPlayer);
                game.setGameOver(true);
                gameData.setGame(game);
                gameData = dbAccess.updateGame(gameData);
            }
            if(!message.contains("moved")) {
                notification = new NotificationMessage(message);
                notifySessions(command.getGameID(), null, gson.toJson(notification));
            }
        }
    }

    private void leaveGame(Session session, String username, UserGameCommand command) throws Exception {
        GameData gameData = dbAccess.getGame(command.getGameID().toString());
        if(gameData == null) {
            throw new Exception("Error: Couldn't find game data. Is the game ID correct?");
        }
        if(gameData.getBlackUsername() != null && gameData.getBlackUsername().equals(username)) {
            gameData.setBlackUsername(null);
        } else if(gameData.getWhiteUsername() != null && gameData.getWhiteUsername().equals(username)) {
            gameData.setWhiteUsername(null);
        } // otherwise it is an observer - no game data update to do
        gameData = dbAccess.updateGame(gameData);
        String message = String.format("%s left the game.", username);
        NotificationMessage notification = new NotificationMessage(message);
        notifySessions(command.getGameID(), session, gson.toJson(notification));
        connections.get(command.getGameID()).remove(session);
        session.close();
    }

    private void resign(Session session, String username, UserGameCommand command) throws Exception {
        GameData gameData = dbAccess.getGame(command.getGameID().toString());
        if(gameData == null) {
            throw new Exception("Error: Couldn't find game data. Is the game ID correct?");
        }
        if(gameData.getBlackUsername() != null && !gameData.getBlackUsername().equals(username)
        && gameData.getWhiteUsername() != null && !gameData.getWhiteUsername().equals(username)) {
            throw new Exception("Error: Observers can't resign!");
        }
        ChessGame game = gameData.getChessGame();
        if(game.isGameOver()) {
            throw new Exception("Error: You can't resign a game that is over!");
        }
        game.setGameOver(true);
        gameData.setGame(game);
        gameData = dbAccess.updateGame(gameData);
        String message = String.format("%s resigned.", username);
        NotificationMessage notification = new NotificationMessage(message);
        notifySessions(command.getGameID(), null, gson.toJson(notification));
    }
}
