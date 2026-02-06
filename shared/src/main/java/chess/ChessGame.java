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
    private boolean[] whiteCanCastle = {true, true};
    private boolean[] blackCanCastle = {true, true};
    private ChessMove lastMove = null;

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
        BLACK;

        @Override
        public String toString() {
            return this == WHITE ? "White":"Black";
        }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) { // TODO: add checks for castling through check
        ChessPiece targetPiece = board.getPiece(startPosition);
        if(targetPiece == null) return null;
        Collection<ChessMove> moves = targetPiece.pieceMoves(board, startPosition);
        ArrayList<ChessMove> toRemove = new ArrayList<ChessMove>();

        // add castles
        if(!isInCheck(targetPiece.getTeamColor()) && targetPiece.getPieceType() == ChessPiece.PieceType.KING) {
            if(targetPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if(whiteCanCastle[0] && board.getPiece(new ChessPosition(1, 4)) == null && board.getPiece(new ChessPosition(1, 3)) == null && board.getPiece(new ChessPosition(1, 2)) == null) {
                    ChessMove castleAttempt = new ChessMove(startPosition, new ChessPosition(1, 4), null);
                    ChessBoard oldBoard = board.clone();
                    board.makeMove(castleAttempt);
                    if(!isInCheck(targetPiece.getTeamColor())) {
                        board = oldBoard;
                        castleAttempt = new ChessMove(startPosition, new ChessPosition(1, 3), null);
                        oldBoard = board.clone();
                        board.makeMove(castleAttempt);
                        if(!isInCheck(targetPiece.getTeamColor())) moves.add(castleAttempt);
                        board = oldBoard;
                    } else board = oldBoard;
                }
                if(whiteCanCastle[1] && board.getPiece(new ChessPosition(1, 6)) == null && board.getPiece(new ChessPosition(1, 7)) == null) {
                    ChessMove castleAttempt = new ChessMove(startPosition, new ChessPosition(1, 6), null);
                    ChessBoard oldBoard = board.clone();
                    board.makeMove(castleAttempt);
                    if(!isInCheck(targetPiece.getTeamColor())) {
                        board = oldBoard;
                        castleAttempt = new ChessMove(startPosition, new ChessPosition(1, 7), null);
                        oldBoard = board.clone();
                        board.makeMove(castleAttempt);
                        if(!isInCheck(targetPiece.getTeamColor())) moves.add(castleAttempt);
                        board = oldBoard;
                    } else board = oldBoard;
                }
            } else {
                if(blackCanCastle[0] && board.getPiece(new ChessPosition(8, 4)) == null && board.getPiece(new ChessPosition(8, 3)) == null && board.getPiece(new ChessPosition(8, 2)) == null) {
                    ChessMove castleAttempt = new ChessMove(startPosition, new ChessPosition(8, 4), null);
                    ChessBoard oldBoard = board.clone();
                    board.makeMove(castleAttempt);
                    if(!isInCheck(targetPiece.getTeamColor())) {
                        board = oldBoard;
                        castleAttempt = new ChessMove(startPosition, new ChessPosition(8, 3), null);
                        oldBoard = board.clone();
                        board.makeMove(castleAttempt);
                        if(!isInCheck(targetPiece.getTeamColor())) moves.add(castleAttempt);
                        board = oldBoard;
                    } else board = oldBoard;
                }
                if(blackCanCastle[1] && board.getPiece(new ChessPosition(8, 6)) == null && board.getPiece(new ChessPosition(8, 7)) == null) {
                    ChessMove castleAttempt = new ChessMove(startPosition, new ChessPosition(8, 6), null);
                    ChessBoard oldBoard = board.clone();
                    board.makeMove(castleAttempt);
                    if(!isInCheck(targetPiece.getTeamColor())) {
                        board = oldBoard;
                        castleAttempt = new ChessMove(startPosition, new ChessPosition(8, 7), null);
                        oldBoard = board.clone();
                        board.makeMove(castleAttempt);
                        if(!isInCheck(targetPiece.getTeamColor())) moves.add(castleAttempt);
                        board = oldBoard;
                    } else board = oldBoard;
                }
            }
        } else if(targetPiece.getPieceType() == ChessPiece.PieceType.PAWN) { // add en passant
            // TODO: en passant
        }

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
    public void makeMove(ChessMove move) throws InvalidMoveException { // TODO: handle en passant
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece targetPiece = board.getPiece(startPosition);
        if(targetPiece != null && targetPiece.getTeamColor() == currentTurn && validMoves(move.getStartPosition()).contains(move)) {

            if(targetPiece.getPieceType() == ChessPiece.PieceType.KING) { // check for castling
                if(currentTurn == ChessGame.TeamColor.WHITE) {
                    if(whiteCanCastle[0] && move.getEndPosition().getColumn() == 3) {
                        board.makeMove(new ChessMove(new ChessPosition(1, 1), new ChessPosition(1, 4), null));
                    } else if(whiteCanCastle[1] && move.getEndPosition().getColumn() == 7) {
                        board.makeMove(new ChessMove(new ChessPosition(1, 8), new ChessPosition(1, 6), null));
                    }
                    whiteCanCastle[0] = false;
                    whiteCanCastle[1] = false;
                } else {
                    if(blackCanCastle[0] && move.getEndPosition().getColumn() == 3) {
                        board.makeMove(new ChessMove(new ChessPosition(8, 1), new ChessPosition(8, 4), null));
                    } else if(blackCanCastle[1] && move.getEndPosition().getColumn() == 7) {
                        board.makeMove(new ChessMove(new ChessPosition(8, 8), new ChessPosition(8, 6), null));
                    }
                    blackCanCastle[0] = false;
                    blackCanCastle[1] = false;
                }
            } else if(targetPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                if(targetPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    if(whiteCanCastle[0] && move.getStartPosition().getColumn() == 1) whiteCanCastle[0] = false;
                    else if(whiteCanCastle[1] && move.getStartPosition().getColumn() == 8) whiteCanCastle[1] = false;
                } else {
                    if(blackCanCastle[0] && move.getStartPosition().getColumn() == 1) blackCanCastle[0] = false;
                    else if(blackCanCastle[1] && move.getStartPosition().getColumn() == 8) blackCanCastle[1] = false;
                }
            }

            board.makeMove(move);
            ChessPiece.PieceType promotionType = move.getPromotionPiece();
            if(promotionType != null) {
                board.addPiece(move.getEndPosition(), new ChessPiece(targetPiece.getTeamColor(), promotionType));
            }
            setTeamTurn((currentTurn == ChessGame.TeamColor.WHITE)? ChessGame.TeamColor.BLACK:ChessGame.TeamColor.WHITE);
            lastMove = move.clone();
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
        if(isInCheck(teamColor) && validMoves(kingPosition).size() == 0) {
            // check for ways to capture the checking piece
            for(int x = 1; x <= 8; x++) {
                for(int y = 1; y <= 8; y++) {
                    ChessPosition currentPosition = new ChessPosition(x, y);
                    ChessPiece currentPiece = board.getPiece(currentPosition);
                    if(currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                        Collection<ChessMove> moves = currentPiece.pieceMoves(board, currentPosition);
                        for(ChessMove move : moves) {
                            ChessBoard oldBoard = board.clone();
                            board.makeMove(move);
                            if(!isInCheck(teamColor)) return false;
                            board = oldBoard;
                        }
                    }
                }
            }
            return true;
        }
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
        if(!isInCheck(teamColor) && validMoves(kingPosition).size() == 0) {
            for(int x = 1; x <= 8; x++) {
                for(int y = 1; y <= 8; y++) {
                    ChessPosition currentPosition = new ChessPosition(x, y);
                    ChessPiece currentPiece = board.getPiece(currentPosition);
                    if(currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                        if(validMoves(currentPosition).size() != 0) return false;
                    }
                }
            }
            return true;
        }
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

    @Override
    public String toString() {
        return String.format("%s\n%s to move.", board.toString(), currentTurn.toString());
    }

    @Override
    public int hashCode() {
        int hash = board.hashCode();
        hash = hash * 31 + currentTurn.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (obj == null || getClass() != obj.getClass()) return false;

        ChessGame toTest = (ChessGame) obj;
        return hashCode() == toTest.hashCode() && board.equals(toTest.getBoard()) && currentTurn == toTest.getTeamTurn();
    }
}
