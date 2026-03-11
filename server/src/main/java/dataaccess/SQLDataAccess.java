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
import com.google.gson.JsonSyntaxException;

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

    public void configureDatabase() throws HttpResponseException {
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

    @Override
    public UserData getUser(String username) throws HttpResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT userDataJson FROM userData WHERE username=?";
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, username);
            ResultSet result = preparedStatement.executeQuery();
            if(result.next()) { // get first entry
                UserData toReturn = gson.fromJson(result.getString("userDataJson"), UserData.class);
                return toReturn;
            } 
            return null;
        } catch (DataAccessException e) {
            throw new InternalServerErrorResponse("Failed to connect to database: " + e.getMessage());
        } catch (SQLException e) {
            throw new InternalServerErrorResponse("Failed to close connection to database or execute query: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            throw new InternalServerErrorResponse("Incorrect json syntax stored in database: " + e.getMessage());
        }
    }

    @Override
    public String createUser(UserData userData) throws HttpResponseException {
        Connection conn = null;
        try (Connection c = DatabaseManager.getConnection()) {
            conn = c;
            conn.setAutoCommit(false);
            String authToken = UUID.randomUUID().toString();
            String statement = "INSERT INTO userData (username, password, email, userDataJson) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            String userDataJson = gson.toJson(userData);
            preparedStatement.setString(1, userData.getUsername());
            preparedStatement.setString(2, userData.getPassword()); // assume stored data is always encrypted
            preparedStatement.setString(3, userData.getEmail());
            preparedStatement.setString(4, userDataJson);
            preparedStatement.executeUpdate();
            conn.commit();
            return authToken;
        } catch (SQLException e) {
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
            } catch (SQLException e1) {
                throw new InternalServerErrorResponse("Connection error: " + e1.getMessage());
            }
            throw new InternalServerErrorResponse("Failed to close connection to database or insert user data: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new InternalServerErrorResponse("Failed connecting to database: " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws HttpResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT authDataJson FROM authData WHERE authToken=?";
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, authToken);
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            AuthData toReturn = gson.fromJson(result.getString("authDataJson"), AuthData.class);
            return toReturn;
        } catch (SQLException e) {
            throw new InternalServerErrorResponse("Failed to close connection or execute query: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new InternalServerErrorResponse("Failed to connect to database: " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(UserData userData) throws HttpResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT authDataJson FROM authData WHERE username=?";
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, userData.getUsername());
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            AuthData toReturn = gson.fromJson(result.getString("authDataJson"), AuthData.class);
            return toReturn;
        } catch (SQLException e) {
            throw new InternalServerErrorResponse("Failed to close connection or execute query: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new InternalServerErrorResponse("Failed to connect to database: " + e.getMessage());
        }
    }

    @Override
    public AuthData createAuth(String authToken, String username) throws HttpResponseException {
        Connection conn = null;
        try (Connection c = DatabaseManager.getConnection()) {
            conn = c;
            conn.setAutoCommit(false);
            String statement = "INSERT INTO authData (authToken, username, authDataJson) VALUES (?, ?, ?)";
            AuthData authData = new AuthData(authToken, username);
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, authToken);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, gson.toJson(authData));
            preparedStatement.executeUpdate();
            conn.commit();
            return authData;
        } catch (SQLException e) {
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
            } catch (SQLException e1) {
                throw new InternalServerErrorResponse("Connection error: " + e1.getMessage());
            }
            throw new InternalServerErrorResponse("Failed to close connection to database or insert auth data: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new InternalServerErrorResponse("Failed connecting to database: " + e.getMessage());
        }
    }

    @Override
    public AuthData createAuth(String username) throws HttpResponseException {
        return createAuth(UUID.randomUUID().toString(), username);
    }

    @Override
    public boolean deleteAuth(AuthData authData) throws HttpResponseException {
        Connection conn = null;
        try (Connection c = DatabaseManager.getConnection()) {
            conn = c;
            conn.setAutoCommit(false);

            String statement = "DELETE FROM authData WHERE authToken=?";
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, authData.getAuthToken());
            preparedStatement.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
            } catch (SQLException e1) {
                throw new InternalServerErrorResponse("Connection error: " + e1.getMessage());
            }
            throw new InternalServerErrorResponse("Failed to close connection to database or insert auth data: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new InternalServerErrorResponse("Failed connecting to database: " + e.getMessage());
        }
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
        } catch (SQLException e) {
            throw new InternalServerErrorResponse("Failed to close connection or execute query: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new InternalServerErrorResponse("Failed to connect to database: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(String gameID) throws HttpResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameDataJson FROM gameData WHERE gameID=?";
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, gameID);
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            return gson.fromJson(result.getString("gameDataJson"), GameData.class);
        } catch (SQLException e) {
            throw new InternalServerErrorResponse("Failed to close connection or execute query: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new InternalServerErrorResponse("Failed to connect to database: " + e.getMessage());
        }
    }

    @Override
    public GameData createGame(String gameName) throws HttpResponseException {
        Connection conn = null;
        try (Connection c = DatabaseManager.getConnection()) {
            conn = c;
            conn.setAutoCommit(false);

            GameData gameData = new GameData(null, null, gameName);
            String statement = 
                "INSERT INTO gameData (gameName, whiteUsername, blackUsername, chessGameJson, gameDataJson) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, gameName);
            preparedStatement.setString(2, null);
            preparedStatement.setString(3, null);
            preparedStatement.setString(4, gson.toJson(gameData.getChessGame()));
            preparedStatement.setString(5, gson.toJson(gameData));
            preparedStatement.executeUpdate();
            ResultSet result = preparedStatement.getGeneratedKeys();
            result.next();
            int gameId = result.getInt(1);
            gameData.setGameID(Integer.toString(gameId));

            statement = "UPDATE gameData SET gameDataJson=? WHERE gameID=?";
            preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, gson.toJson(gameData));
            preparedStatement.setInt(2, gameId);
            preparedStatement.executeUpdate();

            conn.commit();
            return gameData;
        } catch (SQLException e) {
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
            } catch (SQLException e1) {
                throw new InternalServerErrorResponse("Connection error: " + e1.getMessage());
            }
            throw new InternalServerErrorResponse("Failed to close connection to database or insert game data: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new InternalServerErrorResponse("Failed connecting to database: " + e.getMessage());
        }
    }

    @Override
    public GameData updateGame(GameData gameData, UserData userData, String teamColor) throws HttpResponseException {
        Connection conn = null;
        try (Connection c = DatabaseManager.getConnection()) {
            conn = c;
            conn.setAutoCommit(false);

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
                    break;
            }
            if(statement != "") {
                PreparedStatement preparedStatement = conn.prepareStatement(statement);
                preparedStatement.setString(1, userData.getUsername());
                preparedStatement.setString(2, gson.toJson(gameData));
                preparedStatement.setInt(3, Integer.parseInt(gameData.getGameID()));
                preparedStatement.executeUpdate();
            }

            conn.commit();
            return gameData;
        } catch (SQLException e) {
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
            } catch (SQLException e1) {
                throw new InternalServerErrorResponse("Connection error: " + e1.getMessage());
            }
            throw new InternalServerErrorResponse("Failed to close connection to database or update game data: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new InternalServerErrorResponse("Failed connecting to database: " + e.getMessage());
        }
    }

    @Override
    public boolean clearGames() throws HttpResponseException {
        Connection conn = null;
        try (Connection c = DatabaseManager.getConnection()) {
            conn = c;
            conn.setAutoCommit(false);

            String statement = "TRUNCATE TABLE gameData";
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
            } catch (SQLException e1) {
                throw new InternalServerErrorResponse("Connection error: " + e1.getMessage());
            }
            throw new InternalServerErrorResponse("Failed to close connection to database or insert auth data: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new InternalServerErrorResponse("Failed connecting to database: " + e.getMessage());
        }
    }

    @Override
    public boolean clearAuths() throws HttpResponseException {
        Connection conn = null;
        try (Connection c = DatabaseManager.getConnection()) {
            conn = c;
            conn.setAutoCommit(false);

            String statement = "TRUNCATE TABLE authData";
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
            } catch (SQLException e1) {
                throw new InternalServerErrorResponse("Connection error: " + e1.getMessage());
            }
            throw new InternalServerErrorResponse("Failed to close connection to database or insert auth data: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new InternalServerErrorResponse("Failed connecting to database: " + e.getMessage());
        }
    }

    @Override
    public boolean clearUsers() throws HttpResponseException {
        Connection conn = null;
        try (Connection c = DatabaseManager.getConnection()) {
            conn = c;
            conn.setAutoCommit(false);

            String statement = "TRUNCATE TABLE userData";
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
            } catch (SQLException e1) {
                throw new InternalServerErrorResponse("Connection error: " + e1.getMessage());
            }
            throw new InternalServerErrorResponse("Failed to close connection to database or insert auth data: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new InternalServerErrorResponse("Failed connecting to database: " + e.getMessage());
        }
    }

}
