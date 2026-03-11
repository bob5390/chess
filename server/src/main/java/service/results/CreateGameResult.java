package service.results;

import chess.ChessGame;

public class CreateGameResult {
    private String gameID;
    private ChessGame chessGame;

    public CreateGameResult(String gameID, ChessGame chessGame) {
        this.gameID = gameID;
        this.chessGame = chessGame;
    }

    public String getGameID() { return gameID; }
    public ChessGame getChessGame() { return chessGame; }

    @Override
    public boolean equals(Object obj) {
        CreateGameResult toTest = (CreateGameResult) obj;
        return gameID.equals(toTest.getGameID()) && toTest.getChessGame().equals(chessGame);
    }
}
