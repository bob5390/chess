package client;

import java.util.Collection;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import ui.EscapeSequences;

public class BoardDrawer {
    private ChessBoard board;
    private String[][] boardArray;

    public void setBoard(ChessBoard board) { this.board = board; }

    public void drawBoard(String color, Collection<ChessPosition> toHightlight) {
        boardArray = convertBoard();

        if(color.equals("WHITE")) {
            for(int row = 9; row >= 0; row--) {
                for(int col = 0; col < 10; col++) {
                    if(row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                        ChessPosition curSquare = new ChessPosition(row, col);
                        if(toHightlight.contains(curSquare)) {
                            drawSquare(row, col, true);
                        }
                    } else {
                        drawSquare(row, col);
                    }
                }
                System.out.println(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT);
            }
        } else {
            for(int row = 0; row < 10; row++) {
                for(int col = 9; col >= 0; col--) {
                    if(row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                        ChessPosition curSquare = new ChessPosition(row, col);
                        if(toHightlight.contains(curSquare)) {
                            drawSquare(row, col, true);
                        }
                    } else {
                        drawSquare(row, col);
                    }
                }
                System.out.println(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT);
            }
        }
    }

    public void drawBoard(String color) {
        drawBoard(color, null);
    }

    private void drawSquare(int row, int col, boolean highlight) {
        if((row == 0 || row == 9) && col != 0 && col != 9) { // letters
            char currentLetter = (char)(col+'a'-1);
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_BOLD + " " + currentLetter + " ");
        } else if((col == 0 || col == 9) && row != 0 && row != 9) { // numbers
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_BOLD + " " + String.valueOf(row) + " ");
        } else { // board space
            if((row == 0 && col == 0) || (row == 9 && col == 0) || (row == 9 && col == 9) || (row == 0 && col == 9)) {
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY);
            } else {
                if(highlight) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
                } else if((row + col)%2 == 0) { // even summed spaces are black squares
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_BLUE);
                } else { // odd summed spaces are white squares
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_BLUE);
                }
                System.out.print(boardArray[row-1][col-1]);
            }
        }
    }

    private void drawSquare(int row, int col) {
        drawSquare(row, col, false);
    }

    private String[][] convertBoard() {
        String convertedBoard = board.toString();
        convertedBoard = convertedBoard.replace(" ", EscapeSequences.EMPTY);
        convertedBoard = convertedBoard.replace("K", EscapeSequences.WHITE_KING);
        convertedBoard = convertedBoard.replace("Q", EscapeSequences.WHITE_QUEEN);
        convertedBoard = convertedBoard.replace("R", EscapeSequences.WHITE_ROOK);
        convertedBoard = convertedBoard.replace("B", EscapeSequences.WHITE_BISHOP);
        convertedBoard = convertedBoard.replace("N", EscapeSequences.WHITE_KNIGHT);
        convertedBoard = convertedBoard.replace("P", EscapeSequences.WHITE_PAWN);
        convertedBoard = convertedBoard.replace("k", EscapeSequences.BLACK_KING);
        convertedBoard = convertedBoard.replace("q", EscapeSequences.BLACK_QUEEN);
        convertedBoard = convertedBoard.replace("r", EscapeSequences.BLACK_ROOK);
        convertedBoard = convertedBoard.replace("b", EscapeSequences.BLACK_BISHOP);
        convertedBoard = convertedBoard.replace("n", EscapeSequences.BLACK_KNIGHT);
        convertedBoard = convertedBoard.replace("p", EscapeSequences.BLACK_PAWN);
        
        String[] lines = convertedBoard.split("\n");
        String[][] toReturn = new String[8][8];
        int i = 0;
        for(String line : lines) {
            toReturn[i] = line.replaceFirst("\\|", "").split("\\|");
            i++;
        }

        return toReturn;
    }
}
