package client;

import org.junit.jupiter.api.*;

import io.javalin.http.HttpResponseException;
import requests.ClearRequest;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.ClearResult;
import results.CreateGameResult;
import results.JoinGameResult;
import results.ListGamesResult;
import results.LoginResult;
import results.LogoutResult;
import results.RegisterResult;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() {
        facade.clearDatabase(new ClearRequest());
    }


    @Test
    public void testRegisterPass() {
        RegisterResult result = facade.register(new RegisterRequest("user", "password", "email"));
        Assertions.assertNotNull(result.getAuthToken());
        Assertions.assertTrue(result.getUsername().equals("user"));
    }
    @Test
    public void testRegisterFail() {
        facade.register(new RegisterRequest("user", "password", "email"));
        try {
            facade.register(new RegisterRequest("user", "password", "email"));
        } catch(HttpResponseException e) {
            Assertions.assertNotNull(e);
            Assertions.assertEquals("bad response but received body: {\"message\":\"Error: username already taken\"}; reported status: 403", e.getMessage());
        }
    }

    @Test
    public void testLoginPass() {
        facade.register(new RegisterRequest("user", "password", "email"));
        LoginResult result = facade.login(new LoginRequest("user", "password"));
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getAuthToken());
        Assertions.assertEquals("user", result.getUsername());
    }
    @Test
    public void testLoginFail() {
        try {
            facade.login(new LoginRequest("user", "password"));
        } catch(HttpResponseException e) {
            Assertions.assertNotNull(e);
            Assertions.assertEquals("bad response but received body: {\"message\":\"Error: username not found\"}; reported status: 401", e.getMessage());
        }
    }

    @Test
    public void testLogoutPass() {
        RegisterResult register = facade.register(new RegisterRequest("user", "password", "email"));
        LogoutResult result = facade.logout(new LogoutRequest(register.getAuthToken()));
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getSuccess());
    }
    @Test
    public void testLogoutFail() {
        try {
            facade.logout(new LogoutRequest("bad token"));
        } catch(HttpResponseException e) {
            Assertions.assertNotNull(e);
            Assertions.assertEquals("bad response but received body: {\"message\":\"Error: unauthorized\"}; reported status: 401", e.getMessage());
        }
    }

    @Test
    public void testCreateGamePass() {
        RegisterResult register = facade.register(new RegisterRequest("user", "password", "email"));
        CreateGameResult result = facade.createGame(new CreateGameRequest(register.getAuthToken(), "Game 1"));
        Assertions.assertNotNull(result);
        Assertions.assertEquals("1", result.getGameID());
        Assertions.assertNotNull(result.getChessGame());
    }
    @Test
    public void testCreateGameFail() {
        try {
            facade.createGame(new CreateGameRequest("bad token", "Game 1"));
        } catch(HttpResponseException e) {
            Assertions.assertNotNull(e);
            Assertions.assertEquals("bad response but received body: {\"message\":\"Error: unauthorized\"}; reported status: 401", e.getMessage());
        }
    }

    @Test
    public void testJoinGamePass() {
        RegisterResult register = facade.register(new RegisterRequest("user", "password", "email"));
        facade.createGame(new CreateGameRequest(register.getAuthToken(), "Game 1"));
        JoinGameResult result = facade.joinGame(new JoinGameRequest(register.getAuthToken(), "WHITE", "1"));
        Assertions.assertNotNull(result);
        Assertions.assertEquals("user", result.getGameData().getWhiteUsername());
        Assertions.assertEquals("1", result.getGameData().getGameID());
        Assertions.assertEquals("Game 1", result.getGameData().getGameName());
    }
    @Test
    public void testJoinGameFail() {
        RegisterResult register = facade.register(new RegisterRequest("user", "password", "email"));
        facade.createGame(new CreateGameRequest(register.getAuthToken(), "Game 1"));
        facade.joinGame(new JoinGameRequest(register.getAuthToken(), "WHITE", "1"));
        RegisterResult register2 = facade.register(new RegisterRequest("player2", "password`", "email"));
        try {
            facade.joinGame(new JoinGameRequest(register2.getAuthToken(), "WHITE", "1"));
        } catch(HttpResponseException e) {
            Assertions.assertNotNull(e);
            Assertions.assertEquals("bad response but received body: {\"message\":\"Error: cannot join, game already taken\"}; reported status: 403", e.getMessage());
        }
    }

    @Test
    public void testListGamesPass() {
        RegisterResult register = facade.register(new RegisterRequest("user", "password", "email"));
        ListGamesResult result = facade.listGames(new ListGamesRequest(register.getAuthToken()));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getGameList().size());
    }
    @Test
    public void testListGamesFail() {
        try {
            facade.listGames(new ListGamesRequest("bad token"));
        } catch(HttpResponseException e) {
            Assertions.assertNotNull(e);
            Assertions.assertEquals("bad response but received body: {\"message\":\"Error: unauthorized\"}; reported status: 401", e.getMessage());
        }
    }

    @Test
    public void testClearDatabasePass() {
        ClearResult result = facade.clearDatabase(new ClearRequest());
        Assertions.assertNotNull(result);
    }
    @Test
    public void testClearDatabaseLessData() {
        RegisterResult register = facade.register(new RegisterRequest("user", "password", "email"));
        facade.createGame(new CreateGameRequest(register.getAuthToken(), "game 1"));
        facade.createGame(new CreateGameRequest(register.getAuthToken(), "game 2"));
        ListGamesResult listResult = facade.listGames(new ListGamesRequest(register.getAuthToken()));
        Assertions.assertEquals(2, listResult.getGameList().size());
        facade.clearDatabase(new ClearRequest());
        register = facade.register(new RegisterRequest("user", "password", "email"));
        listResult = facade.listGames(new ListGamesRequest(register.getAuthToken()));
        Assertions.assertEquals(0, listResult.getGameList().size());
    }
}
