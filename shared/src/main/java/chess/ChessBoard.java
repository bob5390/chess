package chess;

import java.util.Arrays;

import chess.ChessGame.TeamColor;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

    ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    public void makeMove(ChessMove move) {
        ChessPosition toMove = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        board[endPosition.getRow()-1][endPosition.getColumn()-1] = board[toMove.getRow()-1][toMove.getColumn()-1];
        board[toMove.getRow()-1][toMove.getColumn()-1] = null;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        if(position.getRow() < 1 || position.getRow() > 8 || position.getColumn() < 1 || position.getColumn() > 8) return null;
        
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    public ChessPosition getKingPosition(TeamColor teamColor) {
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                if(board[x][y] != null && board[x][y].getTeamColor() == teamColor && board[x][y].getPieceType() == ChessPiece.PieceType.KING) return new ChessPosition(x+1, y+1);
            }
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // clear the board and add pawns
        for(int y = 1; y <= 8; y++) {
            for(int x = 1; x <= 8; x++) {
                if(x == 2) addPiece(new ChessPosition(x, y), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
                else if(x == 7) addPiece(new ChessPosition(x, y), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
                else board[x-1][y-1] = null;
            }
        }

        // add other pieces
        // ROOK
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

        // KNIGHT
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));

        // BISHOP
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));

        // QUEEN - note: queen always starts on her own square (col 4 for white and for black)
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));

        // KING
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
    }

    @Override
    public String toString() {
        String toReturn = "";
        for(int x = 1; x <= 8; x++) {
            for(int y = 1; y <= 8; y++) {
                toReturn += "|";
                ChessPiece currentPiece = getPiece(new ChessPosition(x, y));
                if(currentPiece == null) {
                    toReturn += " ";
                }
                else if(currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    switch(currentPiece.getPieceType()) {
                        case PAWN -> toReturn += "P";
                        case KNIGHT -> toReturn += "N";
                        case BISHOP -> toReturn += "B";
                        case ROOK -> toReturn += "R";
                        case QUEEN -> toReturn += "Q";
                        case KING -> toReturn += "K";
                    }
                } else {
                    switch(currentPiece.getPieceType()) {
                        case PAWN -> toReturn += "p";
                        case KNIGHT -> toReturn += "n";
                        case BISHOP -> toReturn += "b";
                        case ROOK -> toReturn += "r";
                        case QUEEN -> toReturn += "q";
                        case KING -> toReturn += "k";
                    }
                }
            }
            toReturn += "|\n";
        }
        return toReturn;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        else if(obj == null || getClass() != obj.getClass()) return false;
        
        ChessBoard toTest = (ChessBoard) obj;
        for(int y = 1; y < 8; y++) {
            for(int x = 1; x < 8; x++) {
                ChessPosition currentPosition = new ChessPosition(x, y);
                ChessPiece currentPiece = getPiece(currentPosition);
                ChessPiece toTestPiece = toTest.getPiece(currentPosition);
                if(currentPiece == null && toTestPiece == null) continue;
                else if(currentPiece == null || toTestPiece == null) return false;
                else if(!currentPiece.equals(toTestPiece)) return false;
            }
        }
        return hashCode() == toTest.hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for(int y = 1; y <= 8; y++) {
            for(int x = 1; x <= 8; x++) {
                ChessPiece currentPiece = getPiece(new ChessPosition(x, y));
                hash = 31 * hash + (currentPiece != null ? currentPiece.hashCode() : 0);
            }
        }
        return hash;
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clone;
            clone = (ChessBoard) super.clone();
            clone.board = new ChessPiece[8][8];

            for(int x = 0; x < 8; x++) {
                for(int y = 0; y < 8; y++) {
                    if(board[x][y] == null) clone.board[x][y] = null;
                    else clone.board[x][y] = board[x][y].clone();
                }
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
