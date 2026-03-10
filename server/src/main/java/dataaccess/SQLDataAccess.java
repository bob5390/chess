package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import io.javalin.http.HttpResponseException;
import io.javalin.http.InternalServerErrorResponse;
import model.AuthData;
import model.GameData;
import model.UserData;

public class SQLDataAccess implements DataAccess {
    public SQLDataAccess() {

    }

    private final String[] createStatements = {
        // create auth data table
        """
        CREATE TABLE IF NOT EXISTS authData(
            `authToken` varchar(256) NOT NULL,
            `username` varchar(256) NOT NULL,
            `authDataJson` TEXT NOT NULL
        )
        """,
        // create user data table
        """
        CREATE TABLE IF NOT EXISTS userData(
            `username` varchar(256) NOT NULL,
            `password` varchar(256) NOT NULL,
            `email` varchar(256),
            `userDataJson` TEXT NOT NULL
        )
        """,
        // create game data table
        """
        CREATE TABLE IF NOT EXISTS gameData(
            `gameID` int NOT NULL AUTO_INCREMENT,
            `gameName` varchar(256) NOT NULL,
            `whiteUsername` varchar(256) NOT NULL,
            `blackUsername` varchar(256) NOT NULL,
            `chessGameJson` TEXT NOT NULL,
            `gameDataJson` TEXT NOT NULL
        )
        """
    };

    public void configureDatabase() throws HttpResponseException {
        try {
            DatabaseManager.createDatabase();
            try (Connection conn = DatabaseManager.getConnection()) {
                for(String s : createStatements) {
                    PreparedStatement preparedStatement = conn.prepareStatement(s);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new InternalServerErrorResponse("Failed table creation for database");
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new InternalServerErrorResponse("Failed to create database");
        }
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
