package service.results;

public class CreateGameResult {
    private String gameID;

    public CreateGameResult(String gameID) {
        this.gameID = gameID;
    }

    public String getGameID() { return gameID; }

    @Override
    public boolean equals(Object obj) {
        CreateGameResult toTest = (CreateGameResult) obj;
        return gameID.equals(toTest.getGameID());
    }
}
