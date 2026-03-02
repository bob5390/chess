package service;

import java.util.Map;

import com.google.gson.Gson;

import dataaccess.GameData;

public class JoinGameResult {
    private GameData gameData;

    public JoinGameResult(GameData gameData) {
        this.gameData = gameData;
    }

    public GameData getGameData() { return gameData; }
    public String toJson() {
        return new Gson().toJson(Map.of("success", true));
    }
}
