package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import com.google.gson.Gson;

import io.javalin.http.HttpResponseException;
import io.javalin.http.InternalServerErrorResponse;
import model.AuthData;
import model.GameData;
import model.UserData;

public class SQLDataAccess implements DataAccess {
    private Gson gson;

    public SQLDataAccess() {
        gson = new Gson();
        configureDatabase();
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
            `gameID` int NOT NULL PRIMARY KEY AUTO_INCREMENT,
            `gameName` varchar(256) NOT NULL,
            `whiteUsername` varchar(256),
            `blackUsername` varchar(256),
            `chessGameJson` TEXT NOT NULL,
            `gameDataJson` TEXT
        )
        """
    };

    private void configureDatabase() throws HttpResponseException {
        try {
            DatabaseManager.createDatabase();
            try (Connection conn = DatabaseManager.getConnection()) {
                for(String s : createStatements) {
                    PreparedStatement preparedStatement = conn.prepareStatement(s);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                throw new InternalServerErrorResponse("Failed table creation for database");
            }
        } catch (DataAccessException e) {
            throw new InternalServerErrorResponse("Failed to create database");
        }
    }

    private <T> T getData(String statement, String parameter, String jsonLocation, Class<T> classOf) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, parameter);
            ResultSet result = preparedStatement.executeQuery();
            if(result.next()) {
                T toReturn = gson.fromJson(result.getString(jsonLocation), classOf);
                return toReturn;
            }
            return null;
        } catch (SQLException | DataAccessException e) {
            throw new InternalServerErrorResponse("Connection or query error: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws HttpResponseException {
        String statement = "SELECT userDataJson FROM userData WHERE username=?";
        return getData(statement, username, "userDataJson", UserData.class);
    }

    @Override
    public String createUser(UserData userData) throws HttpResponseException {
        return runTransaction(conn -> {
            String authToken = UUID.randomUUID().toString();
            String statement = "INSERT INTO userData (username, password, email, userDataJson) VALUES (?, ?, ?, ?)";
            String userDataJson = gson.toJson(userData);
            runUpdate(conn, statement, userData.getUsername(), userData.getPassword(), userData.getEmail(), userDataJson);
            return authToken;
        });
    }

    @Override
    public AuthData getAuth(String authToken) throws HttpResponseException {
        String statement = "SELECT authDataJson FROM authData WHERE authToken=?";
        return getData(statement, authToken, "authDataJson", AuthData.class);
    }

    @Override
    public AuthData getAuth(UserData userData) throws HttpResponseException {
        String statement = "SELECT authDataJson FROM authData WHERE username=?";
        return getData(statement, userData.getUsername(), "authDataJson", AuthData.class);
    }

    @Override
    public AuthData createAuth(String authToken, String username) {
        return runTransaction(conn -> {
            String statement = "INSERT INTO authData (authToken, username, authDataJson) VALUES (?, ?, ?)";
            AuthData authData = new AuthData(authToken, username);
            runUpdate(conn, statement, authToken, username, gson.toJson(authData));
            return authData;
        });
    }

    @Override
    public AuthData createAuth(String username) throws HttpResponseException {
        return createAuth(UUID.randomUUID().toString(), username);
    }

    @Override
    public boolean deleteAuth(AuthData authData) throws HttpResponseException {
        return runTransaction(conn -> {
            String statement = "DELETE FROM authData WHERE authToken=?";
            runUpdate(conn, statement, authData.getAuthToken());
            return true;
        });
    }

    @Override
    public Collection<GameData> listGames() throws HttpResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            Collection<GameData> toReturn = new ArrayList<GameData>();
            String statement = "SELECT gameDataJson FROM gameData";
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            ResultSet result = preparedStatement.executeQuery();
            while(result.next()) {
                toReturn.add(gson.fromJson(result.getString("gameDataJson"), GameData.class));
            }
            return toReturn;
        } catch (SQLException | DataAccessException e) {
            throw new InternalServerErrorResponse("Connection or query error: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(String gameID) throws HttpResponseException {
        String statement = "SELECT gameDataJson FROM gameData WHERE gameID=?";
        return getData(statement, gameID, "gameDataJson", GameData.class);
    }

    @Override
    public GameData createGame(String gameName) throws HttpResponseException {
        return runTransaction(conn -> {
            String statement = "INSERT INTO gameData (gameName, whiteUsername, blackUsername, chessGameJson, gameDataJson) VALUES (?, ?, ?, ?, ?)";
            GameData gameData = new GameData(null, null, gameName);
            ResultSet result = runUpdate(conn, statement, gameName, "", "", gson.toJson(gameData.getChessGame()), gson.toJson(gameData));
            result.next();
            int gameID = result.getInt(1);
            gameData.setGameID(Integer.toString(gameID));
            statement = "UPDATE gameData SET gameDataJson=? WHERE gameID=?";
            runUpdate(conn, statement, gson.toJson(gameData), gameID);
            return gameData;
        });
    }

    @Override
    public GameData joinGame(GameData gameData, UserData userData, String teamColor) throws HttpResponseException {
        return runTransaction(conn -> {
            String statement = "";
            switch (teamColor) {
                case "WHITE":
                    statement = "UPDATE gameData SET whiteUsername=?, gameDataJson=? WHERE gameID=?";
                    gameData.setWhiteUsername(userData.getUsername());
                    break;
                case "BLACK":
                    statement = "UPDATE gameData SET blackUsername=?, gameDataJson=? WHERE gameID=?";
                    gameData.setBlackUsername(userData.getUsername());
                    break;
                default:
                    throw new InternalServerErrorResponse("Invalid team color");
            }
            if(statement != "") {
                runUpdate(conn, statement, userData.getUsername(), gson.toJson(gameData), Integer.parseInt(gameData.getGameID()));
            }

            return gameData;
        });
    }

    @Override
    public GameData updateGame(GameData gameData) throws HttpResponseException {
        Connection conn = null;
        try (Connection c = DatabaseManager.getConnection()) {
            conn = c;
            conn.setAutoCommit(false);

            String statement = "UPDATE gameData SET chessGameJson=?, gameDataJson=? WHERE gameID=?";
            runUpdate(conn, statement, gson.toJson(gameData.getChessGame()), gson.toJson(gameData), Integer.parseInt(gameData.getGameID()));
            
            conn.commit();
            return gameData;
        } catch (SQLException | DataAccessException e) {
            throw new InternalServerErrorResponse("Connection or update error: " + e.getMessage());
        }
    }

    @Override
    public boolean clearGames() throws HttpResponseException {
        return runTransaction(conn -> {
            String statement = "TRUNCATE TABLE gameData";
            runUpdate(conn, statement);
            return true;
        });
    }

    @Override
    public boolean clearAuths() throws HttpResponseException {
        return runTransaction(conn -> {
            String statment = "TRUNCATE TABLE authData";
            runUpdate(conn, statment);
            return true;
        });
    }

    @Override
    public boolean clearUsers() throws HttpResponseException {
        return runTransaction(conn -> {
            String statment = "TRUNCATE TABLE userData";
            runUpdate(conn, statment);
            return true;
        });
    }
    
    @FunctionalInterface
    private interface SQLFunction<T, R> {
        R apply(T t) throws SQLException;
    }
    private <T> T runTransaction(SQLFunction<Connection, T> operation) {
        Connection conn = null;
        try (Connection c = DatabaseManager.getConnection()) {
            conn = c;
            conn.setAutoCommit(false);

            T result = operation.apply(conn);

            conn.commit();
            return result;

        } catch (SQLException | DataAccessException e) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
            } catch (SQLException e1) {
                throw new InternalServerErrorResponse("Connection error: " + e1.getMessage());
            }
            throw new InternalServerErrorResponse("Connection or update error: " + e.getMessage());
        }
    }

    private ResultSet runUpdate(Connection conn, String statement, Object... params) throws SQLException{
        PreparedStatement preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS);

        for(int i = 0; i < params.length; i++) {
            Object param = params[i];
            if(param instanceof String s) { preparedStatement.setString(i+1, s); }
            else if(param instanceof Integer n) { preparedStatement.setInt(i+1, n); }
        }
        preparedStatement.executeUpdate();
        return preparedStatement.getGeneratedKeys();
    }
}
