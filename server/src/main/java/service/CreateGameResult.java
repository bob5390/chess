package service;

import java.util.Map;

import com.google.gson.Gson;

import dataaccess.GameData;
import dataaccess.UserData;

public class CreateGameResult {
    private GameData gameData;
    private UserData userData;

    public CreateGameResult(GameData gameData, UserData userData) {
        this.gameData = gameData;
        this.userData = userData;
    }

    public UserData getUserData() { return userData; }
    public GameData getGameData() { return gameData; }
    public String toJson() {
        return new Gson().toJson(Map.of("gameID", gameData.getGameID()));
    }
}
