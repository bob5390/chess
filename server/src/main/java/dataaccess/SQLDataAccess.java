package dataaccess;

import java.util.Collection;

import io.javalin.http.HttpResponseException;
import io.javalin.http.InternalServerErrorResponse;
import model.AuthData;
import model.GameData;
import model.UserData;

public class SQLDataAccess implements DataAccess {
    public SQLDataAccess() {

    }

    public void configureDatabaseTables() {

    }

    public void configureDatabase() throws HttpResponseException {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new InternalServerErrorResponse("Failed to create database");
        }

        configureDatabaseTables();
    }

    @Override
    public UserData getUser(String username) throws HttpResponseException {
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public String createUser(UserData userData) throws HttpResponseException {
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    @Override
    public AuthData getAuth(String authToken) throws HttpResponseException {
        throw new UnsupportedOperationException("Unimplemented method 'getAuth'");
    }

    @Override
    public AuthData getAuth(UserData userData) throws HttpResponseException {
        throw new UnsupportedOperationException("Unimplemented method 'getAuth'");
    }

    @Override
    public AuthData createAuth(String authToken, String username) throws HttpResponseException {
        throw new UnsupportedOperationException("Unimplemented method 'createAuth'");
    }

    @Override
    public AuthData createAuth(String username) throws HttpResponseException {
        throw new UnsupportedOperationException("Unimplemented method 'createAuth'");
    }

    @Override
    public boolean deleteAuth(AuthData authData) throws HttpResponseException {
        throw new UnsupportedOperationException("Unimplemented method 'deleteAuth'");
    }

    @Override
    public Collection<GameData> listGames() throws HttpResponseException {
        throw new UnsupportedOperationException("Unimplemented method 'listGames'");
    }

    @Override
    public GameData getGame(String gameID) throws HttpResponseException {
        throw new UnsupportedOperationException("Unimplemented method 'getGame'");
    }

    @Override
    public GameData createGame(String gameName) throws HttpResponseException {
        throw new UnsupportedOperationException("Unimplemented method 'createGame'");
    }

    @Override
    public GameData updateGame(GameData gameData, UserData userData, String teamColor) throws HttpResponseException {
        throw new UnsupportedOperationException("Unimplemented method 'updateGame'");
    }

    @Override
    public boolean clearGames() throws HttpResponseException {
        throw new UnsupportedOperationException("Unimplemented method 'clearGames'");
    }

    @Override
    public boolean clearAuths() throws HttpResponseException {
        throw new UnsupportedOperationException("Unimplemented method 'clearAuths'");
    }

    @Override
    public boolean clearUsers() throws HttpResponseException {
        throw new UnsupportedOperationException("Unimplemented method 'clearUsers'");
    }

}
