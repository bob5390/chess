package client;

import chess.ChessBoard;
import ui.EscapeSequences;

public class BoardDrawer {
    private ChessBoard board;

    public void setBoard(ChessBoard board) { this.board = board; }

    public void drawBoard(String color) {
        if(color.equals("WHITE")) {
            for(int row = 0; row < 9; row++) {
                for(int col = 0; col < 9; col++) {
                    drawSquare(row, col);
                }
                System.out.println(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            }
        } else {
            for(int row = 8; row >= 0; row--) {
                for(int col = 8; col >= 0; col--) {
                    drawSquare(row, col);
                }
                System.out.println(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            }
        }
    }

    private void drawSquare(int row, int col) {
        if((row == 0 || row == 8) && col != 0 && col != 8) { // letters
            char currentLetter = (char)(col+'a'-1);
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_BOLD + " " + currentLetter + " ");
        } else if((col == 0 || col == 8) && row != 0 && row != 8) { // numbers
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_BOLD + String.valueOf(row));
        } else { // board space
            if((row == 0 && col == 0) || (row == 8 && col == 0) || (row == 8 && col == 8) || (row == 0 && col == 8)) {
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            } else {
                System.out.print(EscapeSequences.SET_BG_COLOR_GREEN);
            }
            System.out.print(EscapeSequences.EMPTY);
        }
    }
}
