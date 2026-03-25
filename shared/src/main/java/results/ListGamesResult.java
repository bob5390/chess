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

    @Override
    public String toString() {
        String toReturn = "";
        int i = 1;
        for(GameData game : games) {
            toReturn += String.format("%d. Game Name: %s White: %s Black: %s", i, game.getGameName(), game.getWhiteUsername(), game.getBlackUsername());
            if(i != games.size()) { toReturn += "\n"; }
            i++;
        }
        return toReturn;
    }
}
