package service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import dataaccess.GameData;

public class ListGamesResult {
    private Collection<GameData> gameList;

    public ListGamesResult(Collection<GameData> gameList) {
        this.gameList = gameList;
    }

    public Collection<GameData> getGameList() { return gameList; }
    public String toJson() {
        List<Map<String, String>> toConvert = new ArrayList();
        for(GameData i : gameList) {
            toConvert.add(i.getMap());
        }
        return new Gson().toJson(toConvert.toArray());
    }
}
