package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int column;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.column = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return column;
    }

    public static boolean validPosition(ChessPosition position) {
        return position.getColumn() >= 1 && position.getColumn() <= 8 && position.getRow() >= 1 && position.getRow() <= 8;
    }

    @Override
    public String toString() {
        return String.format("%c%d", 'a' + column - 1, row);
    }

    @Override
    public boolean equals(Object obj) { 
        if (this == obj) { return true; }
        if (obj == null || getClass() != obj.getClass()) { return false; }
        
        ChessPosition toTest = (ChessPosition) obj;
        return hashCode() == toTest.hashCode() && row == toTest.row && column == toTest.column;
    }

    @Override
    public int hashCode() {
        return row * 31 + column;
    }
}
