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
    public GameData getGameByID(String id) {
        for(GameData game : games) {
            if(game.getGameID().equals(id)) return game;
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        ListGamesResult toTest = (ListGamesResult) obj;
        return games.equals(toTest.getGameList());
    }

    @Override
    public String toString() {
        String toReturn = "";
        games.sort((a,b) -> Integer.compare(Integer.parseInt(a.getGameID()), Integer.parseInt(b.getGameID())));
        for(int i = 0; i < games.size(); i++) {
            toReturn += games.get(i).toString();
            if(i != games.size()-1) toReturn += "\n";
        }
        return toReturn;
    }
}
