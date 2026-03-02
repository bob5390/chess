package service;

import java.util.Map;

import com.google.gson.Gson;

import chess.ChessGame;

public class JoinGameRequest {
    String authToken;
    ChessGame.TeamColor teamColor;
    String gameID;

    public JoinGameRequest(String authToken, String json) {
        this.authToken = authToken;
        Map<String, String> jsonData = new Gson().fromJson(json, Map.class);
        teamColor = (jsonData.get("playerColor") == "WHITE")? ChessGame.TeamColor.WHITE:ChessGame.TeamColor.BLACK;
        gameID = jsonData.get("gameID");
    }

    public String getAuthToken() { return authToken; }
    public String getGameID() { return gameID; }
    public ChessGame.TeamColor getTeamColor() { return teamColor; }
}
