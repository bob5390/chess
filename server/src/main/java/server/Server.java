package server;

import java.util.Map;

import com.google.gson.Gson;

import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import service.ChessService;
import service.requests.ClearRequest;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.ListGamesRequest;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.results.CreateGameResult;
import service.results.JoinGameResult;
import service.results.ListGamesResult;
import service.results.LoginResult;
import service.results.LogoutResult;
import service.results.RegisterResult;

public class Server {
    private final ChessService service;
    private final Gson gson;

    private final Javalin javalin;

    public Server() {
        service = new ChessService(new MemoryDataAccess());
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
               .delete("/db", this::clearDatabases);
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
