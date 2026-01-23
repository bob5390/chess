package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Calculates moves for a bishop piece
 */
public class BishopMoveCalculator implements ChessMoveCalculator {

    /**
     * Calculates all the positions a bishop can move to
     * Does not take into account moves that are illegal due to leaving the king in danger
     * 
     * @param board the chess board to calculate moves on
     * @param myPosition the position of the bishop to calculate moves for
     */
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        
        int startX = myPosition.getRow();
        int startY = myPosition.getColumn();
        // up right
        for (int x = startX + 1, y = startY + 1; x <= 8 && y <= 8; x++, y++) {
            ChessPosition toAdd = new ChessPosition(x, y);
            ChessPiece targetPiece = board.getPiece(toAdd);
            if(targetPiece != null) {
                if(targetPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) moves.add(new ChessMove(myPosition, toAdd, null));
                break;
            }
            moves.add(new ChessMove(myPosition, toAdd, null));
        }
        // up left
        for (int x = startX - 1, y = startY + 1; x >= 1 && y <= 8; x--, y++) {
            ChessPosition toAdd = new ChessPosition(x, y);
            ChessPiece targetPiece = board.getPiece(toAdd);
            if(targetPiece != null) {
                if(targetPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) moves.add(new ChessMove(myPosition, toAdd, null));
                break;
            }
            moves.add(new ChessMove(myPosition, toAdd, null));
        }
        // down right
        for (int x = startX + 1, y = startY - 1; x <= 8 && y >= 1; x++, y--) {
            ChessPosition toAdd = new ChessPosition(x, y);
            ChessPiece targetPiece = board.getPiece(toAdd);
            if(targetPiece != null) {
                if(targetPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) moves.add(new ChessMove(myPosition, toAdd, null));
                break;
            }
            moves.add(new ChessMove(myPosition, toAdd, null));
        }
        // down left
        for (int x = startX - 1, y = startY - 1; x >= 1 && y >= 1; x--, y--) {
            ChessPosition toAdd = new ChessPosition(x, y);
            ChessPiece targetPiece = board.getPiece(toAdd);
            if(targetPiece != null) {
                if(targetPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) moves.add(new ChessMove(myPosition, toAdd, null));
                break;
            }
            moves.add(new ChessMove(myPosition, toAdd, null));
        }

        return moves;
    }

}
