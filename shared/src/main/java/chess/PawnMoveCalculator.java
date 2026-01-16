package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements ChessMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        int startColumn = 0; // consider changing this to inline if logic to limit space complexity
        switch (piece.getTeamColor()) {
            case WHITE:
                startColumn = 2;
                break;
            case BLACK:
                startColumn = 7;
                break;
        }

        ArrayList<ChessMove> moves = new ArrayList<>();
        int startX = myPosition.getRow();
        int startY = myPosition.getColumn();
        // pawns move forward one square
        if (startX < 8) {
            ChessPosition toAdd = new ChessPosition(startX + 1, startY);
            ChessPiece targetPiece = board.getPiece(toAdd);
            if (targetPiece == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(startX + 1, startY), null));
                if(startY == startColumn && startX + 2 <= 8) { // can move two squares on first move
                    toAdd = new ChessPosition(startX + 2, startY);
                    targetPiece = board.getPiece(toAdd);
                    if(targetPiece == null) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(startX + 2, startY), null));
                    }
                }
            }
            // check for captures
            toAdd = new ChessPosition(startX + 1, startY + 1);
            targetPiece = board.getPiece(toAdd);
            if(targetPiece != null && targetPiece.getTeamColor() != piece.getTeamColor()) { // can capture diagonally
                moves.add(new ChessMove(myPosition, new ChessPosition(startX + 1, startY + 1), null));
            }
            toAdd = new ChessPosition(startX + 1, startY - 1);
            targetPiece = board.getPiece(toAdd);
            if(targetPiece != null && targetPiece.getTeamColor() != piece.getTeamColor()) { // can capture diagonally
                moves.add(new ChessMove(myPosition, new ChessPosition(startX + 1, startY - 1), null));
            }
        }

        return moves;
    }
}
