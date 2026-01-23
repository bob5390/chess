package chess.move_calculators;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;


/**
 * Calculates moves for a knight piece
 */
public class KnightMoveCalculator implements ChessMoveCalculator {

    /**
     * Calculates all the positions a knight can move to
     * Does not take into account moves that are illegal due to leaving the king in danger
     * 
     * @param board the chess board to calculate moves on
     * @param myPosition the position of the knight to calculate moves for
     */
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        
        int startX = myPosition.getRow();
        int startY = myPosition.getColumn();
        // all 8 possible L-shaped moves
        int[][] deltas = {
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2},
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
        };
        for (int[] delta : deltas) {
            int newX = startX + delta[0];
            int newY = startY + delta[1];
            if (newX >= 1 && newX <= 8 && newY >= 1 && newY <= 8) { // check if on the board
                ChessPosition newPos = new ChessPosition(newX, newY);
                ChessPiece targetPiece = board.getPiece(newPos);
                if(targetPiece == null || targetPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) { // check if the position is empty
                    moves.add(new ChessMove(myPosition, new ChessPosition(newX, newY), null));
                }
            }
        }

        return moves;
    }
}
