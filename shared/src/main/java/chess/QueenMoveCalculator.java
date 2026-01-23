package chess;

import java.util.Collection;

/**
 * Calculates moves for a queen piece
 */
public class QueenMoveCalculator implements ChessMoveCalculator {

    /**
     * Calculates all the positions a queen can move to
     * Does not take into account moves that are illegal due to leaving the king in danger
     * 
     * @param board the chess board to calculate moves on
     * @param myPosition the position of the queen to calculate moves for
     */
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        RookMoveCalculator rookCalculator = new RookMoveCalculator();
        BishopMoveCalculator bishopCalculator = new BishopMoveCalculator();
        
        Collection<ChessMove> rookMoves = rookCalculator.calculateMoves(board, myPosition);
        Collection<ChessMove> bishopMoves = bishopCalculator.calculateMoves(board, myPosition);
        
        rookMoves.addAll(bishopMoves);
        return rookMoves;
    }
}
