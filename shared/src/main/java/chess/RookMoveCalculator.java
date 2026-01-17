package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCalculator implements ChessMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        
        int startX = myPosition.getRow();
        int startY = myPosition.getColumn();

        // up
        for(int x = startX + 1; x <= 8; x++) {
            ChessPosition toAdd = new ChessPosition(x, startY);
            ChessPiece targetPiece = board.getPiece(toAdd);
            if(targetPiece != null) {
                if(targetPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) moves.add(new ChessMove(myPosition, toAdd, null));
                break;
            }
            moves.add(new ChessMove(myPosition, toAdd, null));
        }
        // down
        for(int x = startX - 1; x >= 1; x--) {
            ChessPosition toAdd = new ChessPosition(x, startY);
            ChessPiece targetPiece = board.getPiece(toAdd);
            if(targetPiece != null) {
                if(targetPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) moves.add(new ChessMove(myPosition, toAdd, null));
                break;
            }
            moves.add(new ChessMove(myPosition, toAdd, null));
        }
        // right
        for (int y = startY + 1; y <= 8; y++) {
            ChessPosition toAdd = new ChessPosition(startX, y);
            ChessPiece targetPiece = board.getPiece(toAdd);
            if(targetPiece != null) {
                if(targetPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) moves.add(new ChessMove(myPosition, toAdd, null));
                break;
            }
            moves.add(new ChessMove(myPosition, toAdd, null));
        }
        // left
        for (int y = startY - 1; y >= 1; y--) {
            ChessPosition toAdd = new ChessPosition(startX, y);
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
