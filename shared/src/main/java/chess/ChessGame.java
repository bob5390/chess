package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTurn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece targetPiece = board.getPiece(startPosition);
        if(targetPiece == null) return null;
        Collection<ChessMove> moves = targetPiece.pieceMoves(board, startPosition);
        ArrayList<ChessMove> toRemove = new ArrayList<ChessMove>();
        for(ChessMove move : moves) {
            ChessBoard oldBoard = board.clone();
            board.makeMove(move);
            if(isInCheck(targetPiece.getTeamColor())) toRemove.add(move);
            board = oldBoard;
        }
        moves.removeAll(toRemove);
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece targetPiece = board.getPiece(startPosition);
        if(targetPiece != null && targetPiece.getTeamColor() == currentTurn && validMoves(move.getStartPosition()).contains(move)) {
            board.makeMove(move);
            ChessPiece.PieceType promotionType = move.getPromotionPiece();
            if(promotionType != null) {
                board.addPiece(move.getEndPosition(), new ChessPiece(targetPiece.getTeamColor(), promotionType));
            }
            setTeamTurn((currentTurn == ChessGame.TeamColor.WHITE)? ChessGame.TeamColor.BLACK:ChessGame.TeamColor.WHITE);
        } else {
            throw new InvalidMoveException("Invalid move " + move.toString());
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = board.getKingPosition(teamColor);

        // check for rook checks
        for(int x = kingPosition.getRow()+1; x <= 8; x++) {
            ChessPosition targetPosition = new ChessPosition(x, kingPosition.getColumn());
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if(targetPiece != null && targetPiece.getTeamColor() != teamColor && (targetPiece.getPieceType() == ChessPiece.PieceType.ROOK || targetPiece.getPieceType() == ChessPiece.PieceType.QUEEN)) return true;
            else if(targetPiece != null && targetPiece.getTeamColor() == teamColor) break;
        }
        for(int x = kingPosition.getRow()-1; x >= 1; x--) {
            ChessPosition targetPosition = new ChessPosition(x, kingPosition.getColumn());
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if(targetPiece != null && targetPiece.getTeamColor() != teamColor && (targetPiece.getPieceType() == ChessPiece.PieceType.ROOK || targetPiece.getPieceType() == ChessPiece.PieceType.QUEEN)) return true;
            else if(targetPiece != null && targetPiece.getTeamColor() == teamColor) break;
        }
        for(int y = kingPosition.getColumn()+1; y <= 8; y++) {
            ChessPosition targetPosition = new ChessPosition(kingPosition.getRow(), y);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if(targetPiece != null && targetPiece.getTeamColor() != teamColor && (targetPiece.getPieceType() == ChessPiece.PieceType.ROOK || targetPiece.getPieceType() == ChessPiece.PieceType.QUEEN)) return true;
            else if(targetPiece != null && targetPiece.getTeamColor() == teamColor) break;
        }
        for(int y = kingPosition.getColumn()-1; y >= 1; y--) {
            ChessPosition targetPosition = new ChessPosition(kingPosition.getRow(), y);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if(targetPiece != null && targetPiece.getTeamColor() != teamColor && (targetPiece.getPieceType() == ChessPiece.PieceType.ROOK || targetPiece.getPieceType() == ChessPiece.PieceType.QUEEN)) return true;
            else if(targetPiece != null && targetPiece.getTeamColor() == teamColor) break;
        }

        // check for bishop checks
        for(int x = kingPosition.getRow()+1, y = kingPosition.getColumn()+1; x <= 8 && y <= 8; x++, y++) {
            ChessPosition targetPosition = new ChessPosition(x, y);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if(targetPiece != null && targetPiece.getTeamColor() != teamColor && (targetPiece.getPieceType() == ChessPiece.PieceType.BISHOP || targetPiece.getPieceType() == ChessPiece.PieceType.QUEEN)) return true;
            if(targetPiece != null && targetPiece.getTeamColor() != teamColor && teamColor == ChessGame.TeamColor.WHITE && x == kingPosition.getRow()+1 && y == kingPosition.getColumn()+1 && targetPiece.getPieceType() == ChessPiece.PieceType.PAWN) return true;
            if(targetPiece != null && targetPiece.getTeamColor() == teamColor) break;
        }
        for(int x = kingPosition.getRow()+1, y = kingPosition.getColumn()-1; x <= 8 && y >= 1; x++, y--) {
            ChessPosition targetPosition = new ChessPosition(x, y);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if(targetPiece != null && targetPiece.getTeamColor() != teamColor && (targetPiece.getPieceType() == ChessPiece.PieceType.BISHOP || targetPiece.getPieceType() == ChessPiece.PieceType.QUEEN)) return true;
            if(targetPiece != null && targetPiece.getTeamColor() != teamColor && teamColor == ChessGame.TeamColor.WHITE && x == kingPosition.getRow()+1 && y == kingPosition.getColumn()-1 && targetPiece.getPieceType() == ChessPiece.PieceType.PAWN) return true;
            if(targetPiece != null && targetPiece.getTeamColor() == teamColor) break;
        }
        for(int x = kingPosition.getRow()-1, y = kingPosition.getColumn()+1; x >= 1 && y <= 8; x--, y++) {
            ChessPosition targetPosition = new ChessPosition(x, y);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if(targetPiece != null && targetPiece.getTeamColor() != teamColor && (targetPiece.getPieceType() == ChessPiece.PieceType.BISHOP || targetPiece.getPieceType() == ChessPiece.PieceType.QUEEN)) return true;
            if(targetPiece != null && targetPiece.getTeamColor() != teamColor && teamColor == ChessGame.TeamColor.BLACK && x == kingPosition.getRow()-1 && y == kingPosition.getColumn()+1 && targetPiece.getPieceType() == ChessPiece.PieceType.PAWN) return true;
            if(targetPiece != null && targetPiece.getTeamColor() == teamColor) break;
        }
        for(int x = kingPosition.getRow()-1, y = kingPosition.getColumn()-1; x >= 1 && y >= 1; x--, y--) {
            ChessPosition targetPosition = new ChessPosition(x, y);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if(targetPiece != null && targetPiece.getTeamColor() != teamColor && (targetPiece.getPieceType() == ChessPiece.PieceType.BISHOP || targetPiece.getPieceType() == ChessPiece.PieceType.QUEEN)) return true;
            if(targetPiece != null && targetPiece.getTeamColor() != teamColor && teamColor == ChessGame.TeamColor.BLACK && x == kingPosition.getRow()-1 && y == kingPosition.getColumn()-1 && targetPiece.getPieceType() == ChessPiece.PieceType.PAWN) return true;
            if(targetPiece != null && targetPiece.getTeamColor() == teamColor) break;
        }

        // check for knight checks
        int[][] deltas = {
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2},
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
        };
        for(int[] delta : deltas) {
            ChessPosition targetPosition = new ChessPosition(kingPosition.getRow()+delta[0], kingPosition.getColumn()+delta[1]);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if(targetPiece != null && targetPiece.getTeamColor() != teamColor && targetPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) return true;
        }

        // check for moving next to a king - not actually check since we can't move there, but useful to have in this function
        for(int x = kingPosition.getRow()-1; x <= kingPosition.getRow()+1; x++) {
            for(int y = kingPosition.getColumn()-1; y <= kingPosition.getColumn()+1; y++) {
                if(x == kingPosition.getRow() && y == kingPosition.getColumn()) continue;
                ChessPosition targetPosition = new ChessPosition(x, y);
                if(ChessPosition.validPosition(targetPosition)) {
                    ChessPiece targetPiece = board.getPiece(targetPosition);
                    if(targetPiece != null && targetPiece.getTeamColor() != teamColor && targetPiece.getPieceType() == ChessPiece.PieceType.KING) return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = board.getKingPosition(teamColor);
        if(isInCheck(teamColor) && validMoves(kingPosition).size() == 0) return true;
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition kingPosition = board.getKingPosition(teamColor);
        if(!isInCheck(teamColor) && validMoves(kingPosition).size() == 0) return true;
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
