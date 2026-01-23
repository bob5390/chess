package chess.move_calculators;

import java.util.Collection;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

/**
 * Interface for calculating chess moves for different piece types
 */
public interface ChessMoveCalculator {
    /**
     * Calculates all the positions a piece can move to
     * Does not take into account moves that are illegal due to leaving the king in danger
     * 
     * @param board the chess board to calculate moves on
     * @param myPosition the position of the piece to calculate moves for
     */
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition);
}
