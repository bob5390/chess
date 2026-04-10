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
    private boolean gameOver = false;
    // private boolean gameStarted = false;

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
            return this == WHITE ? "WHITE":"BLACK";
        }
    }

    private boolean safeMove(ChessMove targetMove, ChessGame.TeamColor teamColor) {
        ChessBoard oldBoard = board.clone();
        board.makeMove(targetMove);
        boolean toReturn = !isInCheck(teamColor);
        board = oldBoard;
        return toReturn;
    }

    private boolean rowOfSquaresEmpty(int row, int... columns) {
        for(int col : columns) {
            if(board.getPiece(new ChessPosition(row, col)) != null) {
                return false;
            }
        }
        return true;
    }

    private Collection<ChessMove> getCastleMoves(ChessPosition startPosition, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> toReturn = new ArrayList<ChessMove>();
        boolean[] canCastle = (teamColor == ChessGame.TeamColor.WHITE) ? whiteCanCastle : blackCanCastle;

        if(canCastle[0] && rowOfSquaresEmpty(startPosition.getRow(), 4, 3, 2)) {
            ChessMove castleAttempt = new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), 4), null);
            if(safeMove(castleAttempt, teamColor)) {
                castleAttempt = new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), 3), null);
                if(safeMove(castleAttempt, teamColor)) {
                    toReturn.add(castleAttempt);
                }
            }
        }
        if(canCastle[1] && rowOfSquaresEmpty(startPosition.getRow(), 6, 7)) {
            ChessMove castleAttempt = new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), 6), null);
            if(safeMove(castleAttempt, teamColor)) {
                castleAttempt = new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), 7), null);
                if(safeMove(castleAttempt, teamColor)) {
                    toReturn.add(castleAttempt);
                }
            }
        }
        return toReturn;
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
        if(targetPiece == null) { return null; }
        Collection<ChessMove> moves = targetPiece.pieceMoves(board, startPosition);
        ArrayList<ChessMove> toRemove = new ArrayList<ChessMove>();

        // add castles
        if(!isInCheck(targetPiece.getTeamColor()) && targetPiece.getPieceType() == ChessPiece.PieceType.KING) {
            moves.addAll(getCastleMoves(startPosition, targetPiece.getTeamColor()));
        } else if(targetPiece.getPieceType() == ChessPiece.PieceType.PAWN && lastMove != null) { // add en passant
            int direction = (targetPiece.getTeamColor() == ChessGame.TeamColor.WHITE)? 1:-1;
            int row = (targetPiece.getTeamColor() == ChessGame.TeamColor.WHITE)? 5:4;
            ChessPiece lastMovedPiece = board.getPiece(lastMove.getEndPosition());
            if(lastMovedPiece.getPieceType() == ChessPiece.PieceType.PAWN 
                && startPosition.getRow() == row && lastMove.getEndPosition().getRow() == row) {
                if(lastMove.getEndPosition().getColumn() == startPosition.getColumn()+1 
                    || lastMove.getEndPosition().getColumn() == startPosition.getColumn()-1) {
                    ChessPosition enPassantTarget = new ChessPosition(row+direction, lastMove.getEndPosition().getColumn());
                    ChessPiece spaceToMoveTo = board.getPiece(enPassantTarget);
                    if(spaceToMoveTo == null) { moves.add(new ChessMove(startPosition, enPassantTarget, null)); }
                }
            }
        }

        for(ChessMove move : moves) {
            if(!safeMove(move, targetPiece.getTeamColor())) { toRemove.add(move); }
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
        if(gameOver) {
            throw new InvalidMoveException("Error: Game is over!");
        }
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
                    if(whiteCanCastle[0] && move.getStartPosition().getColumn() == 1) { whiteCanCastle[0] = false; }
                    else if(whiteCanCastle[1] && move.getStartPosition().getColumn() == 8) { whiteCanCastle[1] = false; }
                } else {
                    if(blackCanCastle[0] && move.getStartPosition().getColumn() == 1) { blackCanCastle[0] = false; }
                    else if(blackCanCastle[1] && move.getStartPosition().getColumn() == 8) { blackCanCastle[1] = false; }
                }
            } else if(targetPiece.getPieceType() == ChessPiece.PieceType.PAWN && lastMove != null) {
                ChessPiece lastMovedPiece = board.getPiece(lastMove.getEndPosition());
                if(lastMovedPiece.getPieceType() == ChessPiece.PieceType.PAWN 
                    && move.getEndPosition().getColumn() == lastMove.getEndPosition().getColumn()) {
                    board.addPiece(lastMove.getEndPosition(), null);
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

    private boolean rookCheck(ChessPiece targetPiece, TeamColor teamColor) {
        return (targetPiece != null 
                && targetPiece.getTeamColor() != teamColor 
                && (targetPiece.getPieceType() == ChessPiece.PieceType.ROOK 
                || targetPiece.getPieceType() == ChessPiece.PieceType.QUEEN));
    }

    private boolean bishopCheck(ChessPiece targetPiece, TeamColor teamColor) {
        return (targetPiece != null 
                && targetPiece.getTeamColor() != teamColor 
                && (targetPiece.getPieceType() == ChessPiece.PieceType.BISHOP 
                || targetPiece.getPieceType() == ChessPiece.PieceType.QUEEN));
    }

    private boolean pawnCheck(ChessPosition targetPosition, ChessPiece targetPiece, ChessPosition kingPosition, TeamColor teamColor, int[] deltas) {
        return (targetPiece != null 
                && targetPiece.getTeamColor() != teamColor 
                && targetPosition.getRow() == kingPosition.getRow()+deltas[0] 
                && targetPosition.getColumn() == kingPosition.getColumn()+deltas[1] 
                && targetPiece.getPieceType() == ChessPiece.PieceType.PAWN);
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
        int[] rookDeltas = {1, -1};

        for(int i : rookDeltas) {
            for(int x = kingPosition.getRow()+i; x >= 1 && x <= 8; x+=i) {
                ChessPosition targetPosition = new ChessPosition(x, kingPosition.getColumn());
                ChessPiece targetPiece = board.getPiece(targetPosition);
                if(rookCheck(targetPiece, teamColor)) { return true; }
                else if(targetPiece != null && targetPiece.getTeamColor() == teamColor) { break; }
            }
            for(int y = kingPosition.getColumn()+i; y >= 1 && y <= 8; y+=i) {
                ChessPosition targetPosition = new ChessPosition(kingPosition.getRow(), y);
                ChessPiece targetPiece = board.getPiece(targetPosition);
                if(rookCheck(targetPiece, teamColor)) { return true; }
                else if(targetPiece != null && targetPiece.getTeamColor() == teamColor) { break; }
            }
        }

        // check for bishop checks
        int[][] bishopDeltas = {
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for(int[] i : bishopDeltas) {
            for(int x = kingPosition.getRow()+i[0], y = kingPosition.getColumn()+i[1]; 
                x >= 1 && x <= 8 && y >= 1 && y <= 8; x+=i[0], y+=i[1]) {
                    ChessPosition targetPosition = new ChessPosition(x, y);
                    ChessPiece targetPiece = board.getPiece(targetPosition);
                    if(bishopCheck(targetPiece, teamColor)) { return true; }
                    else if(pawnCheck(targetPosition, targetPiece, kingPosition, teamColor, i)) {return true; }
                    else if(targetPiece != null && targetPiece.getTeamColor() == teamColor) { break; }
            }
        }

        // check for knight checks
        int[][] knightDeltas = {
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2},
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
        };
        for(int[] delta : knightDeltas) {
            ChessPosition targetPosition = new ChessPosition(kingPosition.getRow()+delta[0], kingPosition.getColumn()+delta[1]);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if(targetPiece != null 
                && targetPiece.getTeamColor() != teamColor 
                && targetPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) { return true; }
        }

        // check for moving next to a king - not actually check since we can't move there, but useful to have in this function
        for(int x = kingPosition.getRow()-1; x <= kingPosition.getRow()+1; x++) {
            for(int y = kingPosition.getColumn()-1; y <= kingPosition.getColumn()+1; y++) {
                if(x == kingPosition.getRow() && y == kingPosition.getColumn()) { continue; }
                ChessPosition targetPosition = new ChessPosition(x, y);
                if(ChessPosition.validPosition(targetPosition)) {
                    ChessPiece targetPiece = board.getPiece(targetPosition);
                    if(targetPiece != null 
                        && targetPiece.getTeamColor() != teamColor 
                        && targetPiece.getPieceType() == ChessPiece.PieceType.KING) { return true; }
                }
            }
        }

        return false;
    }

    private boolean hasSafeMove(Collection<ChessMove> moves, ChessGame.TeamColor teamColor) {
        for(ChessMove move : moves) {
            if(safeMove(move, teamColor)) { return true; }
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
        if(!isInCheck(teamColor)) { return false; }
        ChessPosition kingPosition = board.getKingPosition(teamColor);
        if(validMoves(kingPosition).size() > 0) { return false; }
        // check for ways to capture the checking piece
        for(int x = 1; x <= 8; x++) {
            for(int y = 1; y <= 8; y++) {
                ChessPosition currentPosition = new ChessPosition(x, y);
                if(currentPosition.equals(kingPosition)) { continue; }
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if(currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = currentPiece.pieceMoves(board, currentPosition);
                    if(moves != null && hasSafeMove(moves, teamColor)) { return false; }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)) { return false; }

        for(int x = 1; x <= 8; x++) {
            for(int y = 1; y <= 8; y++) {
                ChessPosition currentPosition = new ChessPosition(x, y);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if(currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                    if(validMoves(currentPosition).size() != 0) { return false; }
                }
            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;

        ChessPiece leftWhiteRook = board.getPiece(new ChessPosition(1, 1));
        ChessPiece rightWhiteRook = board.getPiece(new ChessPosition(1, 8));
        ChessPiece leftBlackRook = board.getPiece(new ChessPosition(8, 1));
        ChessPiece rightBlackRook = board.getPiece(new ChessPosition(8, 8));

        if(board.getKingPosition(ChessGame.TeamColor.WHITE) == null 
            || !board.getKingPosition(ChessGame.TeamColor.WHITE).equals(new ChessPosition(1, 5))) {
            whiteCanCastle[0] = false;
            whiteCanCastle[1] = false;
        } else {
            if(leftWhiteRook == null 
                || leftWhiteRook.getPieceType() != ChessPiece.PieceType.ROOK 
                || leftWhiteRook.getTeamColor() != ChessGame.TeamColor.WHITE) { whiteCanCastle[0] = false; }
            if(rightWhiteRook == null 
                || rightWhiteRook.getPieceType() != ChessPiece.PieceType.ROOK 
                || rightWhiteRook.getTeamColor() != ChessGame.TeamColor.WHITE) { whiteCanCastle[1] = false; }
        }
        if(board.getKingPosition(ChessGame.TeamColor.BLACK) == null 
            || !board.getKingPosition(ChessGame.TeamColor.BLACK).equals(new ChessPosition(8, 5))) {
            blackCanCastle[0] = false;
            blackCanCastle[1] = false;
        } else {
            if(leftBlackRook == null 
                || leftBlackRook.getPieceType() != ChessPiece.PieceType.ROOK 
                || leftBlackRook.getTeamColor() != ChessGame.TeamColor.BLACK) { blackCanCastle[0] = false; }
            if(rightBlackRook == null 
                || rightBlackRook.getPieceType() != ChessPiece.PieceType.ROOK 
                || rightBlackRook.getTeamColor() != ChessGame.TeamColor.BLACK) { blackCanCastle[1] = false; }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isGameOver() {
        return gameOver;
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
        if (this == obj) { return true; }
        else if (obj == null || getClass() != obj.getClass()) { return false; }

        ChessGame toTest = (ChessGame) obj;
        return hashCode() == toTest.hashCode() && board.equals(toTest.getBoard()) && currentTurn == toTest.getTeamTurn();
    }
}
