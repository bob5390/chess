package chess.movecalculators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

/**
 * Calculates moves for a pawn piece
 */
public class PawnMoveCalculator implements ChessMoveCalculator {

    private Collection<ChessMove> promotionMoves(ChessPosition startPosition, ChessPosition targetPosition) {
        return Arrays.asList(
            new ChessMove(startPosition, targetPosition, ChessPiece.PieceType.QUEEN),
            new ChessMove(startPosition, targetPosition, ChessPiece.PieceType.ROOK),
            new ChessMove(startPosition, targetPosition, ChessPiece.PieceType.BISHOP),
            new ChessMove(startPosition, targetPosition, ChessPiece.PieceType.KNIGHT));
    }

    /**
     * Calculates all the positions a pawn can move to
     * Does not take into account moves that are illegal due to leaving the king in danger
     * 
     * @param board the chess board to calculate moves on
     * @param myPosition the position of the pawn to calculate moves for
     */
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        int startRow = 0; // consider changing this to inline if logic to limit space complexity
        int standardDelta = 1;
        switch (piece.getTeamColor()) {
            case WHITE:
                startRow = 2;
                break;
            case BLACK:
                startRow = 7;
                standardDelta = -1;
                break;
        }

        ArrayList<ChessMove> moves = new ArrayList<>();
        int startX = myPosition.getRow();
        int startY = myPosition.getColumn();
        
        ChessPosition toAdd = new ChessPosition(startX + standardDelta, startY);
        // pawns move forward one square
        if(ChessPosition.validPosition(toAdd)) {
            ChessPiece targetPiece = board.getPiece(toAdd);
            if(targetPiece == null) {
                if(toAdd.getRow() == 1 || toAdd.getRow() == 8) {
                    // pawn promotion
                    moves.addAll(promotionMoves(myPosition, toAdd));
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(startX + standardDelta, startY), null));
                }
                if(startX == startRow) { // can move two squares on first move
                    toAdd = new ChessPosition(startX + 2*standardDelta, startY);
                    targetPiece = board.getPiece(toAdd);
                    if(targetPiece == null) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(startX + 2*standardDelta, startY), null));
                    }
                }
            }

            int[] deltas = {1, -1};
            for(int i : deltas) {
                toAdd = new ChessPosition(startX + standardDelta, startY + i);
                targetPiece = board.getPiece(toAdd);
                if(targetPiece != null && targetPiece.getTeamColor() != piece.getTeamColor()) { // can capture diagonally
                    if(toAdd.getRow() == 1 || toAdd.getRow() == 8) {
                        // pawn promotion
                        moves.addAll(promotionMoves(myPosition, toAdd));
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(startX + standardDelta, startY + i), null));
                    }
                }
            }
        }

        return moves;
    }
}
