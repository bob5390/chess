package chess;

import java.util.Collection;

public interface ChessMoveCalculator {
    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition);
}
