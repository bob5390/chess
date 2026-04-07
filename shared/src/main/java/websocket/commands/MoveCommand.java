package websocket.commands;

import chess.ChessMove;

public class MoveCommand extends UserGameCommand {
    ChessMove move;

    public MoveCommand() {
        super(UserGameCommand.CommandType.MAKE_MOVE, null, null);
    }
}
