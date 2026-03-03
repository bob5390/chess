package chess.movecalculators;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

/**
 * Calculates moves for a rook piece
 */
public class RookMoveCalculator implements ChessMoveCalculator { 

    /**
     * Calculates all the positions a rook can move to
     * Does not take into account moves that are illegal due to leaving the king in danger
     * 
     * @param board the chess board to calculate moves on
     * @param myPosition the position of the rook to calculate moves for
     */
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        int[] deltas = {1, -1};

        for(int i : deltas) {
            for(int x = myPosition.getRow()+i; x >= 1 && x <= 8; x+=i) {
                ChessPosition toAdd = new ChessPosition(x, myPosition.getColumn());
                ChessPiece targetPiece = board.getPiece(toAdd);
                if(targetPiece != null) {
                    if(targetPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        moves.add(new ChessMove(myPosition, toAdd, null));
                    }
                    break;
                }
                moves.add(new ChessMove(myPosition, toAdd, null));
            }
            for (int y = myPosition.getColumn()+i; y >= 1 && y <= 8; y+=i) {
                ChessPosition toAdd = new ChessPosition(myPosition.getRow(), y);
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
