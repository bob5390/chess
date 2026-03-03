package service.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.google.gson.Gson;

import model.GameData;

public class ListGamesResult {
    private ArrayList<GameData> games;

    public ListGamesResult(Collection<GameData> games) {
        this.games = new ArrayList<GameData>(games);
        if(this.games == null) {
            this.games = new ArrayList<GameData>();
        }
    }

    public ArrayList<GameData> getGameList() { return games; }
    public GameData[] getGameListArray() { return games.toArray(new GameData[0]); }
    public String toString() {
        return new Gson().toJson(Map.of("games", games.toArray()));
    }
}
