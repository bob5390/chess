package service;

import chess.ChessGame;
import dataaccess.AuthData;
import dataaccess.GameData;
import dataaccess.MemoryDataAccess;
import dataaccess.UserData;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

public class ChessService {
    MemoryDataAccess dbAccess;

    public ChessService() {
        dbAccess = new MemoryDataAccess();
    }

    public RegisterResult register(RegisterRequest req) throws ForbiddenResponse {
        UserData userData = dbAccess.getUser(req.getUsername());
        if(userData != null) {
            throw new ForbiddenResponse("username already taken");
        } else {
            userData = new UserData(req.getUsername(), req.getPassword(), req.getEmail());
            String authToken = dbAccess.createUser(userData);
            AuthData authData = dbAccess.createAuth(authToken, userData.getUsername());
            return new RegisterResult(authData.getAuthToken(), userData.getUsername());
        }
    }

    public LoginResult login(LoginRequest req) {
        UserData userData = dbAccess.getUser(req.getUsername());
        if(userData != null) {
            if(checkPassword(req.getPassword(), userData.getPassword())) {
                AuthData authData = dbAccess.getAuth(userData);
                if(authData == null) {
                    authData = dbAccess.createAuth(userData.getUsername());
                    return new LoginResult(userData.getUsername(), authData.getAuthToken());
                } else {
                    throw new BadRequestResponse("already logged in");
                }
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
            UserData userData = dbAccess.getUser(authData.getUsername());
            GameData gameData = dbAccess.createGame(req.getGameName(), userData);
            return new CreateGameResult(gameData, userData);
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
                if(checkTeamColor(req.getTeamColor(), gameData)) {
                    gameData = dbAccess.updateGame(gameData, userData);
                    return new JoinGameResult(gameData);
                } else {
                    throw new ForbiddenResponse("cannot join, game already taken");
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
