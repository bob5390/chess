package model;

import chess.ChessGame;

public class GameData {
    private String gameID;
    private String blackUsername;
    private String whiteUsername;
    private String gameName;
    private ChessGame chessGame;

    public GameData(String gameID, String blackUsername, String whiteUsername, String gameName) {
        this.gameID = gameID;
        this.blackUsername = blackUsername;
        this.whiteUsername = whiteUsername;
        this.gameName = gameName;
        this.chessGame = new ChessGame();
    }

    public GameData(String blackUsername, String whiteUsername, String gameName) {
        this(null, blackUsername, whiteUsername, gameName);
    }

    public void setGameID(String gameID) { this.gameID = gameID; }
    public void setBlackUsername(String username) { blackUsername = username; }
    public void setWhiteUsername(String username) { whiteUsername = username; }

    public String getGameID() { return gameID; }
    public String getBlackUsername() { return blackUsername; }
    public String getWhiteUsername() { return whiteUsername; }
    public String getGameName() { return gameName; }

    public ChessGame getChessGame() { return chessGame; }
}
