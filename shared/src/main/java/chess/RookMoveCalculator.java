package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCalculator implements ChessMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        
        int startX = myPosition.getRow();
        int startY = myPosition.getColumn();
        // up/down
        for (int x = 1; x <= 8; x++) {
            if(x != startX) moves.add(new ChessMove(myPosition, new ChessPosition(x, startY), null));
        }
        // right/left
        for (int y = 1; y <= 8; y++) {
            if(y != startY) moves.add(new ChessMove(myPosition, new ChessPosition(startX, y), null));
        }

        return moves;
    }
}
