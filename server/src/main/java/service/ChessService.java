package service;

import java.util.Collection;

import dataaccess.AlreadyTakenException;
import dataaccess.AuthData;
import dataaccess.DataAccess;
import dataaccess.GameData;
import dataaccess.MemoryDataAccess;
import dataaccess.UserData;
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
        throw new NotImplementedError();
    }

    public CreateGameResult createGame(CreateGameRequest req) {
        throw new NotImplementedError();
    }

    public JoinGameResult joinGame(JoinGameRequest req) {
        throw new NotImplementedError();
    }

    public ClearResult clearDatabases(ClearRequest req) {
        throw new NotImplementedError();
    }

    private boolean checkPassword(String password1, String password2) {
        return password1.equals(password2);
    }
}
