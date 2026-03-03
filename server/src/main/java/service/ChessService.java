package service;

import dataaccess.MemoryDataAccess;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.HttpResponseException;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.GameData;
import model.UserData;
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

public class ChessService {
    MemoryDataAccess dbAccess;

    public ChessService() {
        dbAccess = new MemoryDataAccess();
    }

    public RegisterResult register(RegisterRequest req) throws HttpResponseException {
        UserData userData = dbAccess.getUser(req.getUsername());
        if(userData != null) {
            throw new ForbiddenResponse("username already taken");
        } else if(req.getUsername() != null) {
            if(req.getPassword() != null) {
                userData = new UserData(req.getUsername(), req.getPassword(), req.getEmail());
                String authToken = dbAccess.createUser(userData);
                AuthData authData = dbAccess.createAuth(authToken, userData.getUsername());
                return new RegisterResult(authData.getAuthToken(), userData.getUsername());
            } else {
                throw new BadRequestResponse("no password provided");
            }
        } else {
            throw new BadRequestResponse("no username provided");
        }
    }

    public LoginResult login(LoginRequest req) throws HttpResponseException {
        UserData userData = dbAccess.getUser(req.getUsername());
        if(userData != null) {
            if(checkPassword(req.getPassword(), userData.getPassword())) {
                AuthData authData = dbAccess.createAuth(userData.getUsername());
                return new LoginResult(userData.getUsername(), authData.getAuthToken());
            } else if(req.getPassword() != null) {
                throw new UnauthorizedResponse("unauthorized");
            } else {
                throw new BadRequestResponse("no password provided");
            }
        } else if(req.getUsername() != null) {
            throw new UnauthorizedResponse("username not found");
        } else {
            throw new BadRequestResponse("username not provided");
        }
    }

    public LogoutResult logout(LogoutRequest req) throws UnauthorizedResponse {
        AuthData authData = dbAccess.getAuth(req.getAuthToken());
        if(authData != null) {
            return new LogoutResult(dbAccess.deleteAuth(authData));
        } else {
            throw new UnauthorizedResponse("unauthorized");
        }
    }

    public ListGamesResult listGames(ListGamesRequest req) throws UnauthorizedResponse {
        AuthData authData = dbAccess.getAuth(req.getAuthToken());
        if(authData != null) {
            return new ListGamesResult(dbAccess.listGames());
        } else {
            throw new UnauthorizedResponse("unauthorized");
        }
    }

    public CreateGameResult createGame(CreateGameRequest req) throws UnauthorizedResponse {
        AuthData authData = dbAccess.getAuth(req.getAuthToken());
        if(authData != null) {
            if(req.getGameName() != null && req.getGameName() != "") {
                GameData gameData = dbAccess.createGame(req.getGameName());
                return new CreateGameResult(gameData.getGameID());
            } else {
                throw new BadRequestResponse("no game name provided");
            }
        } else {
            throw new UnauthorizedResponse("unauthorized");
        }
    }

    public JoinGameResult joinGame(JoinGameRequest req) throws ForbiddenResponse {
        AuthData authData = dbAccess.getAuth(req.getAuthToken());
        if(authData != null) {
            UserData userData = dbAccess.getUser(authData.getUsername());
            GameData gameData = dbAccess.getGame(req.getGameID());
            if(gameData != null) {
                if(req.getTeamColor() != null && (req.getTeamColor().equals("WHITE") || req.getTeamColor().equals("BLACK"))) {
                    if(checkTeamColor(req.getTeamColor(), gameData)) {
                        gameData = dbAccess.updateGame(gameData, userData, req.getTeamColor());
                        return new JoinGameResult();
                    } else {
                        throw new ForbiddenResponse("cannot join, game already taken");
                    }
                } else {
                    throw new BadRequestResponse("invalid team color");
                }
            } else {
                throw new BadRequestResponse("game does not exist");
            }
        } else {
            throw new UnauthorizedResponse("unauthorized");
        }
    }

    public ClearResult clearDatabases(ClearRequest req) throws BadRequestResponse {
        if(dbAccess.clearGames()) {
            if(dbAccess.clearUsers()) {
                if(dbAccess.clearAuths()) {
                    return new ClearResult();
                }
            }
        }
        throw new BadRequestResponse("could not clear databas(es)");
    }

    private boolean checkPassword(String password1, String password2) {
        if(password1 == null || password2 == null) { return false; }
        return password1.equals(password2);
    }

    private boolean checkTeamColor(String teamColor, GameData gameData) {
        if(teamColor.equals("BLACK")) {
            return gameData.getBlackUsername() == null || gameData.getBlackUsername() == "";
        } else {
            return gameData.getWhiteUsername() == null || gameData.getWhiteUsername() == "";
        }
    }
}
