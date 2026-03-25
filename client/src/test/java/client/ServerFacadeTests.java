package client;

import org.junit.jupiter.api.*;

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
        try {
            facade.clearDatabase(new ClearRequest());
        } catch(Exception e) {
            System.out.println("Error: couldn't clear database");
        }
    }


    @Test
    public void testRegisterPass() {
        try {
            RegisterResult result = facade.register(new RegisterRequest("user", "password", "email"));
            Assertions.assertNotNull(result.getAuthToken());
            Assertions.assertTrue(result.getUsername().equals("user"));
        } catch(Exception e) {
            Assertions.assertFalse(true);
        }
    }
    @Test
    public void testRegisterFail() {
        try {
            facade.register(new RegisterRequest("user", "password", "email"));
            facade.register(new RegisterRequest("user", "password", "email"));
        } catch(Exception e) {
            Assertions.assertNotNull(e);
            Assertions.assertEquals("bad response but received body: {\"message\":\"Error: username already taken\"}; reported status: 403",
                                    e.getMessage());
        }
    }

    @Test
    public void testLoginPass() {
        try {
            facade.register(new RegisterRequest("user", "password", "email"));
            LoginResult result = facade.login(new LoginRequest("user", "password"));
            Assertions.assertNotNull(result);
            Assertions.assertNotNull(result.getAuthToken());
            Assertions.assertEquals("user", result.getUsername());
        } catch(Exception e) {
            Assertions.assertFalse(true);
        }
    }
    @Test
    public void testLoginFail() {
        try {
            facade.login(new LoginRequest("user", "password"));
        } catch(Exception e) {
            Assertions.assertNotNull(e);
            Assertions.assertEquals("bad response but received body: {\"message\":\"Error: username not found\"}; reported status: 401",
                                    e.getMessage());
        }
    }

    @Test
    public void testLogoutPass() {
        try {
            RegisterResult register = facade.register(new RegisterRequest("user", "password", "email"));
            LogoutResult result = facade.logout(new LogoutRequest(register.getAuthToken()));
            Assertions.assertNotNull(result);
            Assertions.assertTrue(result.getSuccess());
        } catch(Exception e) {
            Assertions.assertFalse(true);
        }
    }
    @Test
    public void testLogoutFail() {
        try {
            facade.logout(new LogoutRequest("bad token"));
        } catch(Exception e) {
            Assertions.assertNotNull(e);
            Assertions.assertEquals("bad response but received body: {\"message\":\"Error: unauthorized\"}; reported status: 401", e.getMessage());
        }
    }

    @Test
    public void testCreateGamePass() {
        try {
            RegisterResult register = facade.register(new RegisterRequest("user", "password", "email"));
            CreateGameResult result = facade.createGame(new CreateGameRequest(register.getAuthToken(), "Game 1"));
            Assertions.assertNotNull(result);
            Assertions.assertEquals("1", result.getGameID());
            Assertions.assertNotNull(result.getChessGame());
        } catch (Exception e) {
            Assertions.assertFalse(true);
        }
    }
    @Test
    public void testCreateGameFail() {
        try {
            facade.createGame(new CreateGameRequest("bad token", "Game 1"));
        } catch(Exception e) {
            Assertions.assertNotNull(e);
            Assertions.assertEquals("bad response but received body: {\"message\":\"Error: unauthorized\"}; reported status: 401", e.getMessage());
        }
    }

    @Test
    public void testJoinGamePass() {
        try {
            RegisterResult register = facade.register(new RegisterRequest("user", "password", "email"));
            facade.createGame(new CreateGameRequest(register.getAuthToken(), "Game 1"));
            JoinGameResult result = facade.joinGame(new JoinGameRequest(register.getAuthToken(), "WHITE", "1"));
            Assertions.assertNotNull(result);
            Assertions.assertEquals("user", result.getGameData().getWhiteUsername());
            Assertions.assertEquals("1", result.getGameData().getGameID());
            Assertions.assertEquals("Game 1", result.getGameData().getGameName());
        } catch(Exception e) {
            Assertions.assertFalse(true);
        }
    }
    @Test
    public void testJoinGameFail() {
        try {
            RegisterResult register = facade.register(new RegisterRequest("user", "password", "email"));
            facade.createGame(new CreateGameRequest(register.getAuthToken(), "Game 1"));
            facade.joinGame(new JoinGameRequest(register.getAuthToken(), "WHITE", "1"));
            RegisterResult register2 = facade.register(new RegisterRequest("player2", "password`", "email"));
            facade.joinGame(new JoinGameRequest(register2.getAuthToken(), "WHITE", "1"));
        } catch(Exception e) {
            Assertions.assertNotNull(e);
            Assertions.assertEquals("bad response but received body: {\"message\":\"Error: cannot join, game already taken\"}; reported status: 403",
                                    e.getMessage());
        }
    }

    @Test
    public void testListGamesPass() {
        try {
            RegisterResult register = facade.register(new RegisterRequest("user", "password", "email"));
            ListGamesResult result = facade.listGames(new ListGamesRequest(register.getAuthToken()));
            Assertions.assertNotNull(result);
            Assertions.assertEquals(0, result.getGameList().size());
        } catch(Exception e) {
            Assertions.assertFalse(true);
        }
    }
    @Test
    public void testListGamesFail() {
        try {
            facade.listGames(new ListGamesRequest("bad token"));
        } catch(Exception e) {
            Assertions.assertNotNull(e);
            Assertions.assertEquals("bad response but received body: {\"message\":\"Error: unauthorized\"}; reported status: 401", e.getMessage());
        }
    }

    @Test
    public void testClearDatabasePass() {
        try {
            ClearResult result = facade.clearDatabase(new ClearRequest());
            Assertions.assertNotNull(result);
        } catch(Exception e) {
            Assertions.assertFalse(true);
        }
    }
    @Test
    public void testClearDatabaseLessData() {
        try {
            RegisterResult register = facade.register(new RegisterRequest("user", "password", "email"));
            facade.createGame(new CreateGameRequest(register.getAuthToken(), "game 1"));
            facade.createGame(new CreateGameRequest(register.getAuthToken(), "game 2"));
            ListGamesResult listResult = facade.listGames(new ListGamesRequest(register.getAuthToken()));
            Assertions.assertEquals(2, listResult.getGameList().size());
            facade.clearDatabase(new ClearRequest());
            register = facade.register(new RegisterRequest("user", "password", "email"));
            listResult = facade.listGames(new ListGamesRequest(register.getAuthToken()));
            Assertions.assertEquals(0, listResult.getGameList().size());
        } catch(Exception e) {
            Assertions.assertFalse(true);
        }
    }
}
