package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalculator implements ChessMoveCalculator {
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
                        moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                    }
                }
            }
        }

        return moves;
    }
}
