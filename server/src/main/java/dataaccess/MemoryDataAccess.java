package dataaccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import chess.ChessGame;

public class MemoryDataAccess implements DataAccess {
    private HashMap<String, UserData> userDataMap;
    private HashMap<String, GameData> gameDataMap;
    private HashMap<String, AuthData> authDataMap;
    private int gameId;

    public MemoryDataAccess() {
        userDataMap = new HashMap<>();
        gameDataMap = new HashMap<>();
        authDataMap = new HashMap<>();
        gameId = 0;
    }

    @Override
    public UserData getUser(String username) {
        return userDataMap.get(username);
    }

    @Override
    public String createUser(UserData userData) {
        String authToken = UUID.randomUUID().toString();
        userDataMap.put(userData.getUsername(), userData);
        return authToken;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authDataMap.get(authToken);
    }

    public AuthData getAuth(UserData userData) {
        for(AuthData i : authDataMap.values()) {
            if(i.getUsername() == userData.getUsername()) return i;
        }
        return null;
    }

    @Override
    public AuthData createAuth(String authToken, String username) {
        AuthData toReturn = new AuthData(authToken, username);
        authDataMap.put(authToken, toReturn);
        return toReturn;
    }

    @Override
    public AuthData createAuth(String username) {
        AuthData toReturn = new AuthData(UUID.randomUUID().toString(), username);
        authDataMap.put(toReturn.getAuthToken(), toReturn);
        return toReturn;
    }

    @Override
    public boolean deleteAuth(AuthData authData) {
        return (authDataMap.remove(authData.getAuthToken()) == null)? false:true;
    }

    @Override
    public Collection<GameData> listGames() {
        Collection<GameData> toReturn = new ArrayList<GameData>();
        for(GameData i : gameDataMap.values()) {
            toReturn.add(i);
        }
        return toReturn;
    }

    @Override
    public GameData getGame(String gameID) {
        return gameDataMap.get(gameID);
    }

    @Override
    public GameData createGame(String gameName) {
        GameData newGame = new GameData(Integer.toString(gameId++), null, null, gameName);
        gameDataMap.put(newGame.getGameID(), newGame);
        return newGame;
    }

    @Override
    public GameData updateGame(GameData gameData, UserData userData, String teamColor) {
        gameDataMap.remove(gameData.getGameID());
        switch (teamColor) {
            case "BLACK":
                gameData.setBlackUsername(userData.getUsername());
                break;
            case "WHITE":
                gameData.setWhiteUsername(userData.getUsername());
                break;
            default:
                break;
        }
        gameDataMap.put(gameData.getGameID(), gameData);
        return gameData;
    }

    @Override
    public boolean clearGames() {
        throw new UnsupportedOperationException("Unimplemented method 'clearGames'");
    }

    @Override
    public boolean clearAuths() {
        throw new UnsupportedOperationException("Unimplemented method 'clearAuths'");
    }

    @Override
    public boolean clearUsers() {
        throw new UnsupportedOperationException("Unimplemented method 'clearUsers'");
    }
}
