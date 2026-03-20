package requests;

public class JoinGameRequest {
    String authToken;
    String playerColor;
    String gameID;

    public JoinGameRequest(String authToken, String teamColor, String gameID) {
        this.authToken = authToken;
        this.playerColor = teamColor;
        this.gameID = gameID;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    public String getAuthToken() { return authToken; }
    public String getGameID() { return gameID; }
    public String getTeamColor() { return playerColor; }
}
