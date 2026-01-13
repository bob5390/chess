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
            moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
        }
        // up left
        for (int x = startX - 1, y = startY + 1; x >= 1 && y <= 8; x--, y++) {
            moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
        }
        // down right
        for (int x = startX + 1, y = startY - 1; x <= 8 && y >= 1; x++, y--) {
            moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
        }
        // down left
        for (int x = startX - 1, y = startY - 1; x >= 1 && y >= 1; x--, y--) {
            moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
        }

        return moves;
    }

}
