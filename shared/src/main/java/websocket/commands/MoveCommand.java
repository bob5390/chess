package websocket.commands;

import chess.ChessMove;

public class MoveCommand extends UserGameCommand {
    ChessMove move;

    public MoveCommand(ChessMove move) {
        super(UserGameCommand.CommandType.MAKE_MOVE, null, null);
        this.move = move;
    }

    public ChessMove getMove() { return move; }
}
