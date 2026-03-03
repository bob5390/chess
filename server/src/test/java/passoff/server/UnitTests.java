package passoff.server;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.GameData;
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

public class UnitTests {
    ChessService service = new ChessService();

    @Test
    public void testRegister() {
        RegisterResult result = service.register(new RegisterRequest("username", "password", "email"));
        RegisterResult expected = new RegisterResult(result.getAuthToken(), "username");

        assert result.getAuthToken() != null;
        assert expected.equals(result);
    }

    @Test
    public void testRegisterBadRequest() {
        try {
            service.register(new RegisterRequest("", "password", "email"));
        } catch (BadRequestResponse e) {
            assert e != null;
            assert e.getMessage().equals("no username provided");
        }
    }

    @Test
    public void testLogin() {
        service.register(new RegisterRequest("username", "password", "email"));
        LoginResult result = service.login(new LoginRequest("username", "password"));
        LoginResult expected = new LoginResult("username", result.getAuthToken());

        assert result.getAuthToken() != null;
        assert expected.equals(result);
    }

    @Test
    public void testLoginUnauthorized() {
        service.register(new RegisterRequest("username", "password", "email"));
        try {
            service.login(new LoginRequest("username", "pw"));
        } catch (UnauthorizedResponse e) {
            assert e != null;
            assert e.getMessage().equals("unauthorized");
        }
    }

    @Test
    public void testLogout() {
        RegisterResult registration = service.register(new RegisterRequest("username", "password", "email"));
        LogoutResult result = service.logout(new LogoutRequest(registration.getAuthToken()));
        LogoutResult expected = new LogoutResult(true);

        assert expected.equals(result);
    }

    @Test
    public void testLogoutUnauthorized() {
        service.register(new RegisterRequest("username", "password", "email"));
        try {
            service.logout(new LogoutRequest("test"));
        } catch (UnauthorizedResponse e) {
            assert e != null;
            assert e.getMessage().equals("unauthorized");
        }
    }

    @Test
    public void testCreateGame() {
        RegisterResult registration = service.register(new RegisterRequest("username", "password", "email"));
        CreateGameResult result = service.createGame(new CreateGameRequest(registration.getAuthToken(), "Game 1"));
        CreateGameResult expected = new CreateGameResult("1");

        assert expected.equals(result);
    }

    @Test
    public void testCreateGameInvalidName() {
        RegisterResult registration = service.register(new RegisterRequest("username", "password", "email"));
        try {
            service.createGame(new CreateGameRequest(registration.getAuthToken(), ""));
        } catch (BadRequestResponse e) {
            assert e.getMessage().equals("no game name provided");
        }
    }

    @Test
    public void testListGames() {
        RegisterResult registration = service.register(new RegisterRequest("username", "password", "email"));
        ListGamesResult result = service.listGames(new ListGamesRequest(registration.getAuthToken()));
        ListGamesResult expected = new ListGamesResult(new ArrayList<GameData>());

        assert expected.equals(result);
    }

    @Test
    public void testListGamesUnauthorized() {
        service.register(new RegisterRequest("username", "password", "email"));
        try {
            service.listGames(new ListGamesRequest("test"));
        } catch (UnauthorizedResponse e) {
            assert e != null;
            assert e.getMessage().equals("unauthorized");
        }
    }

    @Test
    public void testJoinGame() {
        RegisterResult registration = service.register(new RegisterRequest("username", "password", "email"));
        service.createGame(new CreateGameRequest(registration.getAuthToken(), "Game 1"));
        JoinGameResult result = service.joinGame(new JoinGameRequest(registration.getAuthToken(), "WHITE", "1"));

        assert result != null;
    }

    @Test
    public void testJoinGameAlreadyTaken() {
        RegisterResult registration1 = service.register(new RegisterRequest("username1", "password", "email"));
        RegisterResult registration2 = service.register(new RegisterRequest("username2", "password", "email"));
        service.createGame(new CreateGameRequest(registration1.getAuthToken(), "Game 1"));
        try {
            service.joinGame(new JoinGameRequest(registration1.getAuthToken(), "WHITE", "1"));
            service.joinGame(new JoinGameRequest(registration2.getAuthToken(), "WHITE", "1"));
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
