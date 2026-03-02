package model;

import java.util.Map;

public class GameData {
    private String gameID;
    private String blackUsername;
    private String whiteUsername;
    private String gameName;

    public GameData(String gameID, String blackUsername, String whiteUsername, String gameName) {
        this.gameID = gameID;
        this.blackUsername = blackUsername;
        this.whiteUsername = whiteUsername;
        this.gameName = gameName;
    }

    public void setBlackUsername(String username) { blackUsername = username; }
    public void setWhiteUsername(String username) { whiteUsername = username; }

    public String getGameID() { return gameID; }
    public String getBlackUsername() { return blackUsername; }
    public String getWhiteUsername() { return whiteUsername; }
    public String getGameName() { return gameName; }
    public Map<String, String> getMap() { 
        return Map.of("gameID", gameID, "whiteUsername", whiteUsername, "blackUsername", blackUsername, "gameName", gameName);
    }
}
