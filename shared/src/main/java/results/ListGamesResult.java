package results;

import java.util.ArrayList;
import java.util.Collection;

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

    @Override
    public boolean equals(Object obj) {
        ListGamesResult toTest = (ListGamesResult) obj;
        return games.equals(toTest.getGameList());
    }
}
