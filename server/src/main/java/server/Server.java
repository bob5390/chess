package server;

import java.util.Map;

import com.google.gson.Gson;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import requests.ClearRequest;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.CreateGameResult;
import results.JoinGameResult;
import results.ListGamesResult;
import results.LoginResult;
import results.LogoutResult;
import results.RegisterResult;
import server.websocket.WebSocketHandler;
import service.ChessService;

public class Server {
    private final ChessService service;
    private final WebSocketHandler wsHandler;
    private final Gson gson;

    private final Javalin javalin;

    public Server() {
        service = new ChessService(new SQLDataAccess());
        wsHandler = new WebSocketHandler();
        gson = new Gson();
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.exception(HttpResponseException.class, this::exceptionHandler)
               .get("/game", this::listGames)
               .post("/game", this::createGame)
               .post("/user", this::registerUser)
               .post("/session", this::login)
               .put("/game", this::joinGame)
               .delete("/session", this::logout)
               .delete("/db", this::clearDatabases)
               .ws("/ws", ws -> {
                    ws.onConnect(wsHandler);
                    ws.onMessage(wsHandler);
                    ws.onClose(wsHandler);
               });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(HttpResponseException ex, Context ctx) {
        ctx.status(ex.getStatus()); // http status code from ex
        ctx.result(gson.toJson(Map.of("message", "Error: "+ex.getMessage()))); // json result from ex
    }

    private void registerUser(Context ctx) throws HttpResponseException {
        RegisterRequest request = gson.fromJson(ctx.body().strip(), RegisterRequest.class);
        RegisterResult result = service.register(request);
        ctx.status(200);
        ctx.result(gson.toJson(result));
    }

    private void login(Context ctx) throws HttpResponseException {
        LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
        LoginResult result = service.login(request);
        ctx.result(gson.toJson(result));
    }

    private void logout(Context ctx) throws HttpResponseException {
        LogoutResult result = service.logout(new LogoutRequest(ctx.header("Authorization")));
        ctx.result(gson.toJson(result));
    }

    private void listGames(Context ctx) throws HttpResponseException {
        ListGamesResult result = service.listGames(new ListGamesRequest(ctx.header("Authorization")));
        ctx.result(gson.toJson(result));
    }

    private void createGame(Context ctx) throws HttpResponseException {
        CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);
        request.setAuthToken(ctx.header("Authorization"));
        CreateGameResult result = service.createGame(request);
        ctx.result(gson.toJson(result));
    }

    private void joinGame(Context ctx) throws HttpResponseException {
        JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
        request.setAuthToken(ctx.header("Authorization"));
        JoinGameResult result = service.joinGame(request);
        ctx.result(gson.toJson(result));
    }

    private void clearDatabases(Context ctx) throws HttpResponseException {
        ctx.result(gson.toJson(service.clearDatabases(new ClearRequest())));
    }
}
