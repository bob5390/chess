package websocket.commands;

import chess.ChessMove;

public class MoveCommand extends UserGameCommand {
    ChessMove move;

    public MoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() { return move; }
}
