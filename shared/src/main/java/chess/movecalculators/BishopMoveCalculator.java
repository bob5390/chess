package chess.movecalculators;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

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

        int[][] deltas = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};

        for(int[] i : deltas) {
            for(int x = myPosition.getRow()+i[0], y = myPosition.getColumn()+i[1]; 
                x >= 1 && x <= 8 && y >= 1 && y <= 8; x+=i[0], y+=i[1]) {
                    ChessPosition toAdd = new ChessPosition(x, y);
                    ChessPiece targetPiece = board.getPiece(toAdd);
                    if(targetPiece != null) {
                        if(targetPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                            moves.add(new ChessMove(myPosition, toAdd, null));
                        }
                        break;
                    }
                    moves.add(new ChessMove(myPosition, toAdd, null));
            }
        }

        return moves;
    }

}
