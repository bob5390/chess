package server;

import java.util.Map;

import com.google.gson.Gson;

import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import service.ChessService;
import service.ClearRequest;
import service.CreateGameRequest;
import service.CreateGameResult;
import service.JoinGameRequest;
import service.JoinGameResult;
import service.ListGamesRequest;
import service.ListGamesResult;
import service.LoginRequest;
import service.LoginResult;
import service.LogoutRequest;
import service.LogoutResult;
import service.RegisterRequest;
import service.RegisterResult;

public class Server {
    private final ChessService service;
    private final Gson gson;

    private final Javalin javalin;

    public Server() {
        service = new ChessService();
        gson = new Gson();
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.get("/game", this::listGames)
               .post("/game", this::createGame)
               .post("/user", this::registerUser)
               .post("/session", this::login)
               .put("/game", this::joinGame)
               .delete("/session", this::logout)
               .delete("/db", this::clearDatabases)
               .exception(HttpResponseException.class, this::exceptionHandler);
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
        ctx.result(gson.toJson(ex.getMessage())); // json result from ex
    }

    private void listGames(Context ctx) throws HttpResponseException {
        ListGamesResult result = service.listGames(new ListGamesRequest(ctx.header("Authorization")));
        ctx.result(result.toJson());
    }

    private void createGame(Context ctx) throws HttpResponseException {
        Map<String, String> body = gson.fromJson(ctx.body(), Map.class);
        CreateGameRequest request = new CreateGameRequest(ctx.header("Authorization"), body.get("gameName"));
        CreateGameResult result = service.createGame(request);
        ctx.result(result.toJson());
    }

    private void joinGame(Context ctx) throws HttpResponseException {
        JoinGameRequest request = new JoinGameRequest(ctx.header("Authorization"), ctx.body());
        JoinGameResult result = service.joinGame(request);
        ctx.result(result.toJson());
    }

    private void clearDatabases(Context ctx) throws HttpResponseException {
        ctx.result(service.clearDatabases(new ClearRequest()).toJson());
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
}
