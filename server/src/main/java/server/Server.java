package server;

import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import service.ChessService;

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
               .delete("/db", this::clearDatabases)
               .exception(DataAccessException.class, this::exceptionHandler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(DataAccessException ex, Context ctx) {
        ctx.status(ex.toHttpStatusCode()); // http status code from ex
        ctx.result(ex.toJson()); // json result from ex
    }

    private void listGames(Context ctx) throws DataAccessException {
        // ctx.result(service.listGames().toString());
    }

    private void createGame(Context ctx) throws DataAccessException {
        // make game from json
        // make create game request and send off to handler
        // result to json
        ctx.result();
    }

    private void joinGame(Context ctx) throws DataAccessException {
        // make join game request
        // send off to handler
        // result to json
        ctx.result();
    }

    private void clearDatabases(Context ctx) throws DataAccessException {
        // clear request
        // send off to handler
        // result to json
        ctx.result();
    }

    private void registerUser(Context ctx) throws DataAccessException {
        // make register request
        // send off to handler
        // result to json
        ctx.result();
    }

    private void login(Context ctx) throws DataAccessException {
        // login request
        // send to handler
        // result to json
        ctx.result();
    }

    private void logout(Context ctx) throws DataAccessException {
        // logout request
        // send to handler
        // result to json
        ctx.result();
    }
}
