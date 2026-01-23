package chess.move_calculators;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

/**
 * Calculates moves for a king piece
 */
public class KingMoveCalculator implements ChessMoveCalculator {

    /**
     * Calculates all the positions a king can move to
     * Does not take into account moves that are illegal due to leaving the king in danger
     * 
     * @param board the chess board to calculate moves on
     * @param myPosition the position of the king to calculate moves for
     */
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        
        int startX = myPosition.getRow();
        int startY = myPosition.getColumn();
        // all 8 possible directions
        for(int x = startX - 1; x <= startX + 1; x++) {
            for(int y = startY - 1; y <= startY + 1; y++) {
                if(x >= 1 && x <= 8 && y >= 1 && y <= 8) {
                    if(!(x == startX && y == startY)) {
                        ChessPosition toAdd = new ChessPosition(x, y);
                        ChessPiece targetPiece = board.getPiece(toAdd);
                        if(targetPiece == null || targetPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                        }
                    }
                }
            }
        }

        return moves;
    }
}
