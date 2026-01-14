package chess;

import java.util.Collection;

public class QueenMoveCalculator implements ChessMoveCalculator {
    @Override
    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        RookMoveCalculator rookCalculator = new RookMoveCalculator();
        BishopMoveCalculator bishopCalculator = new BishopMoveCalculator();
        
        Collection<ChessMove> rookMoves = rookCalculator.calculateMoves(board, myPosition);
        Collection<ChessMove> bishopMoves = bishopCalculator.calculateMoves(board, myPosition);
        
        rookMoves.addAll(bishopMoves);
        return rookMoves;
    }
}
