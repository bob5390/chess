package service;

import java.util.Collection;

import chess.ChessGame;
import dataaccess.AlreadyTakenException;
import dataaccess.AuthData;
import dataaccess.DataAccess;
import dataaccess.GameData;
import dataaccess.MemoryDataAccess;
import dataaccess.UserData;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
import kotlin.NotImplementedError;

public class ChessService {
    DataAccess dbAccess;

    public ChessService() {
        dbAccess = new MemoryDataAccess();
    }

    public RegisterResult register(RegisterRequest req) throws AlreadyTakenException {
        UserData userData = dbAccess.getUser(req.getUsername());
        if(userData != null) {
            throw new AlreadyTakenException("username already taken");
        } else {
            userData = dbAccess.createUser(userData);
            AuthData authData = dbAccess.createAuth(userData);
            return new RegisterResult(authData, userData);
        }
    }

    public LoginResult login(LoginRequest req) {
        UserData userData = dbAccess.getUser(req.getUsername());
        if(userData != null) {
            if(checkPassword(req.getPassword(), userData.getPassword())) {
                AuthData authData = dbAccess.getAuth(userData);
                return new LoginResult(authData, userData);
            } else {
                throw new UnauthorizedResponse("unauthorized");
            }
        } else {
            throw new UnauthorizedResponse("username not found");
        }
    }

    public LogoutResult logout(LogoutRequest req) {
        AuthData authData = dbAccess.getAuth(req.getAuthToken());
        if(authData != null) {
            return new LogoutResult(dbAccess.deleteAuth(authData));
        } else {
            throw new UnauthorizedResponse("unauthorized");
        }
    }

    public ListGamesResult listGames(ListGamesRequest req) {
        AuthData authData = dbAccess.getAuth(req.getAuthToken());
        if(authData != null) {
            return new ListGamesResult(dbAccess.listGames());
        } else {
            throw new UnauthorizedResponse("unauthorized");
        }
    }

    public CreateGameResult createGame(CreateGameRequest req) {
        AuthData authData = dbAccess.getAuth(req.getAuthToken());
        if(authData != null) {
            UserData userData = dbAccess.getUser(authData);
            GameData gameData = dbAccess.createGame(req.getGameName(), userData);
            return new CreateGameResult(gameData, userData);
        } else {
            throw new UnauthorizedResponse("unauthorized");
        }
    }

    public JoinGameResult joinGame(JoinGameRequest req) throws AlreadyTakenException {
        AuthData authData = dbAccess.getAuth(req.getAuthToken());
        if(authData != null) {
            UserData userData = dbAccess.getUser(authData);
            GameData gameData = dbAccess.getGame(req.getGameID());
            if(gameData != null) {
                if(checkTeamColor(req.getTeamColor(), gameData)) {
                    gameData = dbAccess.updateGame(gameData, userData);
                    return new JoinGameResult(gameData);
                } else {
                    throw new AlreadyTakenException("cannot join, game already taken");
                }    
            } else {
                throw new BadRequestResponse("bad request");
            }
        } else {
            throw new UnauthorizedResponse("unauthorized");
        }
    }

    public ClearResult clearDatabases(ClearRequest req) {
        if(dbAccess.clearGames()) {
            if(dbAccess.clearUsers()) {
                if(dbAccess.clearAuths()) {
                    return new ClearResult(true);
                }
            }
        }
        throw new BadRequestResponse("could not clear databas(es)");
    }

    private boolean checkPassword(String password1, String password2) {
        return password1.equals(password2);
    }

    private boolean checkTeamColor(ChessGame.TeamColor teamColor, GameData gameData) {
        if(teamColor == ChessGame.TeamColor.BLACK) {
            return gameData.getBlackUsername() == "";
        } else {
            return gameData.getWhiteUsername() == "";
        }
    }
}
