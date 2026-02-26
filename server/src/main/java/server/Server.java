package server;

import exception.ResponseException;
import io.javalin.*;

public class Server {
    private final ChessService service;

    private final Javalin javalin;

    public Server() {
        service = new ChessService();
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.before(null)
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

    private void listGames(Context ctx) throws ResponseException {
        ctx.result(service.listGames().toString());
    }

    private void createGame(Context ctx) throws ResponseException {
        // make game from json
        // make create game request and send off to handler
        // result to json
        ctx.result();
    }

    private void joinGame(Context ctx) throws ResponseException {
        // make join game request
        // send off to handler
        // result to json
        ctx.result();
    }

    private void clearDatabases(Context ctx) throws ResponseException {
        // clear request
        // send off to handler
        // result to json
        ctx.result();
    }

    private void registerUser(Context ctx) throws ResponseException {
        // make register request
        // send off to handler
        // result to json
        ctx.result();
    }

    private void login(Context ctx) throws ResponseException {
        // login request
        // send to handler
        // result to json
        ctx.result();
    }

    private void logout(Context ctx) throws ResponseException {
        // logout request
        // send to handler
        // result to json
        ctx.result();
    }
}
