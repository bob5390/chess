package dataaccess;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import io.javalin.http.HttpResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

public class SQLDAOTests {
    static SQLDataAccess dataAccess = new SQLDataAccess();

    private void cleanDatabase() {
        dataAccess.clearAuths();
        dataAccess.clearGames();
        dataAccess.clearUsers();
    }

    @AfterEach
    public void clearDatabase() {
        cleanDatabase();
    }

    @Test
    public void testGetUserPass() {
        UserData expected = new UserData("username", "password", "email");
        dataAccess.createUser(expected);
        UserData result = dataAccess.getUser("username");

        assert expected.equals(result);
    }
    @Test
    public void testGetUserFail() {
        try {
            dataAccess.getUser("thisUserDoesNotExist");
        } catch (HttpResponseException e) {
            assert e != null;
            assert e.getStatus() == 500;
        }
    }

    @Test
    public void testCreateUserPass() {
        UserData toCreate = new UserData("username", "password", "email");
        String result = dataAccess.createUser(toCreate);
        assert result != null;
    }
    @Test
    public void testRegisterFail() {
        UserData toCreate = new UserData("user1", "password", "email");
        String result1 = dataAccess.createUser(toCreate);
        assert result1 != null;
        try {
            dataAccess.createUser(toCreate);
        } catch (HttpResponseException e) {
            assert e != null;
            assert e.getStatus() == 500;
        }
    }

    @Test
    public void testGetAuthByAuthTokenPass() {
        dataAccess.createUser(new UserData("user", "password", "email"));
        AuthData expected = dataAccess.createAuth("user");
        AuthData result = dataAccess.getAuth(expected.getAuthToken());
        assert expected.equals(result);
    }
    @Test
    public void testGetAuthByAuthTokenFail() {
        dataAccess.createUser(new UserData("user", "password", "email"));
        dataAccess.createAuth("user");
        AuthData result = dataAccess.getAuth("badToken");
        assert result == null;
    }
    @Test
    public void testGetAuthByUserDataPass() {
        UserData user = new UserData("user", "password", "email");
        dataAccess.createUser(user);
        AuthData expected = dataAccess.createAuth("user");
        AuthData result = dataAccess.getAuth(user);
        assert expected.equals(result);
    }
    @Test
    public void testGetAuthByUserDataFail() {
        dataAccess.createUser(new UserData("user", "password", "email"));
        dataAccess.createAuth("user");
        AuthData result = dataAccess.getAuth(new UserData("badUser", "pass", "email"));
        assert result == null;
    }

    @Test
    public void testCreateAuthGivenTokenPass() {
        String token = dataAccess.createUser(new UserData("user", "password", "email"));
        AuthData result = dataAccess.createAuth(token, "user");
        AuthData expected = new AuthData(token, "user");
        assert expected.equals(result);
    }
    @Test
    public void testCreateAuthGivenTokenMultiple() {
        String token = dataAccess.createUser(new UserData("user", "password", "email"));
        AuthData result = dataAccess.createAuth(token, "user");
        AuthData expected = new AuthData(token, "user");
        assert expected.equals(result);
        AuthData result2 = dataAccess.createAuth(token, "user");
        assert expected.equals(result2);
    }
    @Test
    public void testCreateAuthUsernameOnlyPass() {
        AuthData result = dataAccess.createAuth("user");
        AuthData expected = new AuthData(result.getAuthToken(), "user");
        assert expected.equals(result);
    }
    @Test
    public void testCreateAuthUsernameOnlyMultiple() {
        AuthData result = dataAccess.createAuth("user");
        AuthData expected = new AuthData(result.getAuthToken(), "user");
        assert expected.equals(result);
        AuthData result2 = dataAccess.createAuth("user");
        assert result2.getUsername().equals(expected.getUsername());
        assert !result2.getAuthToken().equals(expected.getAuthToken());
    }

    @Test
    public void testDeleteAuthPass() {
        AuthData toDelete = dataAccess.createAuth("user");
        boolean result = dataAccess.deleteAuth(toDelete);
        assert result;
    }
    @Test
    public void testDeleteAuthNotInTable() {
        boolean result = dataAccess.deleteAuth(new AuthData("token", "user"));
        assert result;
    }

    @Test
    public void testListGamesPass() {
        Collection<GameData> result = dataAccess.listGames();
        assert result.size() == 0;
    }
    @Test
    public void testListGamesManyGames() {
        dataAccess.createGame("game 1");
        dataAccess.createGame("game 2");
        dataAccess.createGame("game 3");
        Collection<GameData> result1 = dataAccess.listGames();
        assert result1.size() == 3;
        dataAccess.createGame("game 4");
        Collection<GameData> result2 = dataAccess.listGames();
        assert result2.size() == 4;
    }

    @Test
    public void testGetGamePass() {
        GameData expected = dataAccess.createGame("game 1");
        expected.setGameID("1");
        GameData result = dataAccess.getGame("1");
        assert expected.equals(result);
    }
    @Test
    public void testGetGameFail() {
        GameData result = dataAccess.getGame("1");
        assert result == null;
    }

    @Test
    public void testCreateGamePass() {
        GameData result = dataAccess.createGame("Game 1");
        GameData expected = new GameData("1", null, null, "Game 1");
        assert expected.equals(result);
    }
    @Test
    public void testCreateGameSameName() {
        GameData result1 = dataAccess.createGame("Game");
        GameData result2 = dataAccess.createGame("Game");
        GameData expected1 = new GameData("1", null, null, "Game");
        GameData expected2 = new GameData("2", null, null, "Game");
        assert expected1.equals(result1);
        assert expected2.equals(result2);
    }

    @Test
    public void testJoinGamePass() {
        GameData game = dataAccess.createGame("Game");
        UserData user = new UserData("user", "password", "email");
        dataAccess.createUser(user);
        GameData result = dataAccess.joinGame(game, user, "WHITE");
        game.setWhiteUsername(user.getUsername());
        assert game.equals(result);
    }
    @Test
    public void testJoinGameFail() {
        GameData game = dataAccess.createGame("Game");
        UserData user = new UserData("user", "password", "email");
        dataAccess.createUser(user);
        try {
            dataAccess.joinGame(game, user, "PURPLE");
        } catch (HttpResponseException e) {
            assert e != null;
            assert e.getStatus() == 500;
        }
    }

    private ChessGame makeMoves(ChessGame game, ChessMove... moves) {
        for(ChessMove m : moves) {
            try {
                game.makeMove(m);
            } catch (InvalidMoveException e) {}
        }
        return game;
    }

    @Test
    public void testUpdateGamePass() {
        GameData gameData = dataAccess.createGame("Game");
        ChessPosition startPosition = new ChessPosition(2, 4);
        ChessPosition targetPosition = new ChessPosition(4, 4);
        ChessMove toMake = new ChessMove(startPosition, targetPosition, null);
        ChessGame game = gameData.getChessGame();
        game = makeMoves(game, toMake);
        GameData result = dataAccess.updateGame(gameData);
        assert result.getChessGame().equals(game);
    }
    @Test
    public void testUpdateGameMultipleMoves() {
        GameData gameData = dataAccess.createGame("Game");
        ChessPosition startPosition = new ChessPosition(2, 4);
        ChessPosition targetPosition = new ChessPosition(4, 4);
        ChessMove toMake = new ChessMove(startPosition, targetPosition, null);
        startPosition = new ChessPosition(7, 4);
        targetPosition = new ChessPosition(5, 4);
        ChessMove toMake2 = new ChessMove(startPosition, targetPosition, null);
        ChessGame game = gameData.getChessGame();
        game = makeMoves(game, toMake, toMake2);
        GameData result = dataAccess.updateGame(gameData);
        assert result.getChessGame().equals(game);
    }
    
    @Test
    public void testClearGames() {
        dataAccess.createGame("Game");
        Collection<GameData> gameList = dataAccess.listGames();
        assert gameList.size() == 1;
        assert dataAccess.clearGames();
        gameList = dataAccess.listGames();
        assert gameList.size() == 0;
    }

    @Test
    public void testClearAuths() {
        AuthData auth = dataAccess.createAuth("user");
        AuthData inTable = dataAccess.getAuth(auth.getAuthToken());
        assert inTable.equals(auth);
        assert dataAccess.clearAuths();
        inTable = dataAccess.getAuth(auth.getAuthToken());
        assert inTable == null;
    }

    @Test
    public void testClearUsers() {
        UserData user = new UserData("user", "password", "email");
        dataAccess.createUser(user);
        UserData inTable = dataAccess.getUser("user");
        assert inTable.equals(user);
        assert dataAccess.clearUsers();
        inTable = dataAccess.getUser("user");
        assert inTable == null;
    }
}
