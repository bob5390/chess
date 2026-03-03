package passoff.server;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

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
    public void testLogin() {
        service.register(new RegisterRequest("username", "password", "email"));
        LoginResult result = service.login(new LoginRequest("username", "password"));
        LoginResult expected = new LoginResult("username", result.getAuthToken());

        assert result.getAuthToken() != null;
        assert expected.equals(result);
    }

    @Test
    public void testLogout() {
        RegisterResult registration = service.register(new RegisterRequest("username", "password", "email"));
        LogoutResult result = service.logout(new LogoutRequest(registration.getAuthToken()));
        LogoutResult expected = new LogoutResult(true);

        assert expected.equals(result);
    }

    @Test
    public void testCreateGame() {
        RegisterResult registration = service.register(new RegisterRequest("username", "password", "email"));
        CreateGameResult result = service.createGame(new CreateGameRequest(registration.getAuthToken(), "Game 1"));
        CreateGameResult expected = new CreateGameResult("1");

        assert expected.equals(result);
    }

    @Test
    public void testListGames() {
        RegisterResult registration = service.register(new RegisterRequest("username", "password", "email"));
        ListGamesResult result = service.listGames(new ListGamesRequest(registration.getAuthToken()));
        ListGamesResult expected = new ListGamesResult(new ArrayList<GameData>());

        assert expected.equals(result);
    }

    @Test
    public void testJoinGame() {
        RegisterResult registration = service.register(new RegisterRequest("username", "password", "email"));
        service.createGame(new CreateGameRequest(registration.getAuthToken(), "Game 1"));
        JoinGameResult result = service.joinGame(new JoinGameRequest(registration.getAuthToken(), "WHITE", "1"));

        assert result != null;
    }

    @Test
    public void testClearDatabase() {
        ClearResult result = service.clearDatabases(new ClearRequest());

        assert result != null;
    }
}
