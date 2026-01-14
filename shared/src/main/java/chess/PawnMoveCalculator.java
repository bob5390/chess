package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements ChessMoveCalculator {
    @Override
    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        ArrayList<ChessMove> moves = new ArrayList<>();
        int startX = myPosition.getRow();
        int startY = myPosition.getColumn();
        // pawns move forward one square
        if (startX < 8) {
            moves.add(new ChessMove(myPosition, new ChessPosition(startX + 1, startY), null));
        }

        // if at starting position, can move forward two squares
        int startColumn = 0; // consider changing this to inline if logic to limit space complexity
        switch (piece.getTeamColor()) {
            case WHITE:
                startColumn = 2;
                break;
            case BLACK:
                startColumn = 7;
                break;
        }
        if(startY == startColumn && startX + 2 <= 8) {
            moves.add(new ChessMove(myPosition, new ChessPosition(startX + 2, startY), null));
        }

        return moves;
    }
}
