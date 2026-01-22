package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        switch(piece.getPieceType()) {
            case KING:
                return new KingMoveCalculator().calculateMoves(board, myPosition);
            case QUEEN:
                return new QueenMoveCalculator().calculateMoves(board, myPosition);
            case BISHOP:
                return new BishopMoveCalculator().calculateMoves(board, myPosition);
            case KNIGHT:
                return new KnightMoveCalculator().calculateMoves(board, myPosition);
            case ROOK:
                return new RookMoveCalculator().calculateMoves(board, myPosition);
            case PAWN:
                return new PawnMoveCalculator().calculateMoves(board, myPosition);
            default:
                throw new RuntimeException("Unknown piece type: " + piece.getPieceType());
        }
    }

    @Override
    public boolean equals(Object obj) { // TODO: check hashcode as well
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ChessPiece toTest = (ChessPiece) obj;
        return pieceColor == toTest.pieceColor && type == toTest.type;
    }
}
