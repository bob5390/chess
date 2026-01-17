package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalculator implements ChessMoveCalculator {
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
