package results;

import model.GameData;

public class JoinGameResult {
    private GameData gameData;

    public JoinGameResult(GameData gameData) {
        this.gameData = gameData;
    }

    public GameData getGameData() { return gameData; }

    @Override
    public boolean equals(Object obj) {
        JoinGameResult toTest = (JoinGameResult)obj;
        return toTest != null && this.gameData.equals(toTest.getGameData());
    }
}
