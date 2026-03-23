package client;

import org.junit.jupiter.api.*;

import kotlin.NotImplementedError;
import requests.RegisterRequest;
import results.RegisterResult;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        facade = new ServerFacade("http://localhost:0");
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void testRegisterPass() {
        RegisterResult result = facade.register(new RegisterRequest("user", "password", "email"));
        Assertions.assertNotNull(result.getAuthToken());
        Assertions.assertTrue(result.getUsername() == "user");
    }
    @Test
    public void testRegisterFail() {
        throw new NotImplementedError();
    }

    @Test
    public void testLoginPass() {
        throw new NotImplementedError();
    }
    @Test
    public void testLoginFail() {
        throw new NotImplementedError();
    }

    @Test
    public void testLogoutPass() {
        throw new NotImplementedError();
    }
    @Test
    public void testLogoutFail() {
        throw new NotImplementedError();
    }

    @Test
    public void testCreateGamePass() {
        throw new NotImplementedError();
    }
    @Test
    public void testCreateGameFail() {
        throw new NotImplementedError();
    }

    @Test
    public void testJoinGamePass() {
        throw new NotImplementedError();
    }
    @Test
    public void testJoinGameFail() {
        throw new NotImplementedError();
    }

    @Test
    public void testListGamesPass() {
        throw new NotImplementedError();
    }
    @Test
    public void testListGamesFail() {
        throw new NotImplementedError();
    }
}
