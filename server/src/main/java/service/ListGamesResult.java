package service;

import java.util.Collection;

import dataaccess.GameData;

public class ListGamesResult {
    private Collection<GameData> gameList;

    public ListGamesResult(Collection<GameData> gameList) {
        this.gameList = gameList;
    }

    public Collection<GameData> getGameList() { return gameList; }
}
