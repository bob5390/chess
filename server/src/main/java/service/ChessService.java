package service;

import java.util.Collection;

import dataaccess.AlreadyTakenException;
import dataaccess.AuthData;
import dataaccess.DataAccess;
import dataaccess.GameData;
import dataaccess.UserData;
import kotlin.NotImplementedError;

public class ChessService {
    DataAccess dbAccess;

    public ChessService() {
        dbAccess = new DataAccess();
    }

    public RegisterResult register(RegisterRequest req) throws AlreadyTakenException {
        UserData userData = dbAccess.getUser(req.getUsername());
        if(userData != null) {
            throw new AlreadyTakenException("username already taken");
        } else {
            userData = dbAccess.createUser(userData);
            AuthData authData = dbAccess.createAuth(new AuthData(userData.getAuthToken()));
            return new RegisterResult(authData);
        }
    }

    public LoginResult login(LoginRequest req) {
        throw new NotImplementedError();
    }

    public LogoutResult logout(LogoutRequest req) {
        throw new NotImplementedError();
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
}
