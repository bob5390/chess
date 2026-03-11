package dataaccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.GameData;
import server.Server;
import service.ChessService;
import service.requests.ClearRequest;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.ListGamesRequest;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.results.ClearResult;
import service.results.CreateGameResult;
import service.results.JoinGameResult;
import service.results.ListGamesResult;
import service.results.LoginResult;
import service.results.LogoutResult;
import service.results.RegisterResult;

public class SQLDAOTests {
    static Server server;
    static ChessService service = new ChessService(new SQLDataAccess());

    @BeforeAll
    public static void startServer() {
        server = new Server();
        server.run(0);
    }

    @BeforeEach
    public void setupForTest() {
        service.clearDatabases(new ClearRequest());

        RegisterResult reg1 = service.register(new RegisterRequest("user1", "password", "email"));
        RegisterResult reg2 = service.register(new RegisterRequest("user2", "password", "email2"));
        LoginResult login1 = service.login(new LoginRequest(reg1.getUsername(), "password"));
        service.createGame(new CreateGameRequest(login1.getAuthToken(), "game 1"));
        service.joinGame(new JoinGameRequest(login1.getAuthToken(), "WHITE", "1"));
        LoginResult login2 = service.login(new LoginRequest(reg2.getUsername(), "password"));
        service.joinGame(new JoinGameRequest(login2.getAuthToken(), "BLACK", "1"));
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void testRegisterPass() {
        RegisterResult result = service.register(new RegisterRequest("username", "password", "email"));
        RegisterResult expected = new RegisterResult(result.getAuthToken(), "username");

        assert result.getAuthToken() != null;
        assert expected.equals(result);

        RegisterResult result2 = service.register(new RegisterRequest("user3", "password", "email"));
        RegisterResult expected2 = new RegisterResult(result2.getAuthToken(), "user3");

        assert result2.getAuthToken() != null;
        assert expected2.equals(result2);
    }
    @Test
    public void testRegisterFail() {
        service.register(new RegisterRequest("username", "password", "email"));
        stopServer();
        startServer();
        try {
            service.register(new RegisterRequest("username", "password", "email"));
        } catch (ForbiddenResponse e) {
            assert e != null;
            assert e.getMessage().equals("username already taken");
        }
    }

    @Test
    public void testLoginPass() {
        service.register(new RegisterRequest("username", "password", "email"));
        LoginResult result = service.login(new LoginRequest("username", "password"));
        LoginResult expected = new LoginResult("username", result.getAuthToken());

        assert result.getAuthToken() != null;
        assert expected.equals(result);

        LoginResult result2 = service.login(new LoginRequest("user1", "password"));
        LoginResult expected2 = new LoginResult("user1", result2.getAuthToken());

        assert result2.getAuthToken() != null;
        assert expected2.equals(result2);
    }
    @Test
    public void testLoginFail() {
        service.register(new RegisterRequest("username", "password", "email"));
        try {
            service.login(new LoginRequest("fakeUsername", "pw"));
        } catch (UnauthorizedResponse e) {
            assert e != null;
            assert e.getMessage().equals("username not found");
        }
    }

    @Test
    public void testLogoutPass() {
        RegisterResult registration = service.register(new RegisterRequest("username", "password", "email"));
        LogoutResult result = service.logout(new LogoutRequest(registration.getAuthToken()));
        LogoutResult expected = new LogoutResult(true);

        assert expected.equals(result);

        LoginResult login = service.login(new LoginRequest("user1", "password"));

        assert login.getAuthToken() != null;

        LogoutResult result2 = service.logout(new LogoutRequest(login.getAuthToken()));
        LogoutResult expected2 = new LogoutResult(true);

        assert expected2.equals(result2);
    }
    @Test
    public void testLogoutFail() {
        service.register(new RegisterRequest("username", "password", "email"));
        try {
            service.logout(new LogoutRequest("test"));
        } catch (UnauthorizedResponse e) {
            assert e != null;
            assert e.getMessage().equals("unauthorized");
        }

        LoginResult login = service.login(new LoginRequest("user1", "password"));

        assert login.getAuthToken() != null;

        LogoutResult result = service.logout(new LogoutRequest(login.getAuthToken()));
        LogoutResult expected = new LogoutResult(true);

        assert expected.equals(result);

        try {
            service.logout(new LogoutRequest(login.getAuthToken()));
        } catch (UnauthorizedResponse e) {
            assert e != null;
            assert e.getMessage().equals("unauthorized");
        }
    }

    @Test
    public void testCreateGamePass() {
        RegisterResult registration = service.register(new RegisterRequest("username", "password", "email"));
        CreateGameResult result = service.createGame(new CreateGameRequest(registration.getAuthToken(), "Game 1"));
        CreateGameResult expected = new CreateGameResult("2", result.getChessGame());

        assert expected.equals(result);

        LoginResult login = service.login(new LoginRequest("user1", "password"));

        assert login.getAuthToken() != null;

        CreateGameResult result2 = service.createGame(new CreateGameRequest(login.getAuthToken(), "game 2"));
        CreateGameResult expected2 = new CreateGameResult("3", result2.getChessGame());

        assert expected2.equals(result2);
    }
    @Test
    public void testCreateGameFail() {
        service.register(new RegisterRequest("username", "password", "email"));
        try {
            service.createGame(new CreateGameRequest("fakeToken", "testGame"));
        } catch (UnauthorizedResponse e) {
            assert e.getMessage().equals("unauthorized");
        }
    }

    @Test
    public void testListGamesPass() {
        RegisterResult registration = service.register(new RegisterRequest("username", "password", "email"));
        ListGamesResult result = service.listGames(new ListGamesRequest(registration.getAuthToken()));

        assert result.getGameList().size() == 1;

        service.createGame(new CreateGameRequest(registration.getAuthToken(), "another game"));
        ListGamesResult result2 = service.listGames(new ListGamesRequest(registration.getAuthToken()));

        assert result2.getGameList().size() == 2;
    }
    @Test
    public void testListGamesFail() {
        service.register(new RegisterRequest("username", "password", "email"));
        try {
            service.listGames(new ListGamesRequest("test"));
        } catch (UnauthorizedResponse e) {
            assert e != null;
            assert e.getMessage().equals("unauthorized");
        }

        LoginResult login = service.login(new LoginRequest("user2", "password"));

        assert login.getAuthToken() != null;

        service.logout(new LogoutRequest(login.getAuthToken()));

        try {
            service.listGames(new ListGamesRequest(login.getAuthToken()));
        } catch (UnauthorizedResponse e) {
            assert e != null;
            assert e.getMessage().equals("unauthorized");
        }
    }

    @Test
    public void testJoinGamePass() {
        RegisterResult registration = service.register(new RegisterRequest("username", "password", "email"));
        CreateGameResult createGame = service.createGame(new CreateGameRequest(registration.getAuthToken(), "Game 1"));
        JoinGameResult result = service.joinGame(new JoinGameRequest(registration.getAuthToken(), "WHITE", "2"));
        JoinGameResult expected = new JoinGameResult(new GameData(createGame.getGameID(), null, "username", "Game 1"));

        assert result != null;
        assert expected.equals(result);

        JoinGameResult result2 = service.joinGame(new JoinGameRequest(registration.getAuthToken(), "BLACK", "2"));
        JoinGameResult expected2 = new JoinGameResult(new GameData("2", "username", "username", "Game 1"));

        assert result2 != null;
        assert expected2.equals(result2);
    }
    @Test
    public void testJoinGameFail() {
        LoginResult login1 = service.login(new LoginRequest("user1", "password"));
        LoginResult login2 = service.login(new LoginRequest("user2", "password"));
        service.createGame(new CreateGameRequest(login1.getAuthToken(), "Game 1"));
        try {
            service.joinGame(new JoinGameRequest(login2.getAuthToken(), "WHITE", "1"));
        } catch (ForbiddenResponse e) {
            assert e != null;
            assert e.getMessage().equals("cannot join, game already taken");
        }
        try {
            service.joinGame(new JoinGameRequest(login1.getAuthToken(), "PURPLE", "2"));
        } catch (BadRequestResponse e) {
            assert e != null;
            assert e.getMessage().equals("invalid team color");
        }
        service.joinGame(new JoinGameRequest(login1.getAuthToken(), "WHITE", "2"));
        try {
            service.joinGame(new JoinGameRequest(login2.getAuthToken(), "WHITE", "2"));
        } catch (ForbiddenResponse e) {
            assert e != null;
            assert e.getMessage().equals("cannot join, game already taken");
        }
    }

    @Test
    public void testClearDatabase() {
        ClearResult result = service.clearDatabases(new ClearRequest());

        assert result != null;
    }
}
