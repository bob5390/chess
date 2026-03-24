package client;

import chess.ChessBoard;

public class BoardDrawer {
    private ChessBoard board;

    public BoardDrawer(ChessBoard board) {
        this.board = board;
    }

    public void drawBoard(String color) {
        int direction = 1;
        if(color.equals("BLACK")) direction = -1;

        // some for loop that reverses based on direction
        for(int row = 0; row < 9; row++) {
            for(int col = 0; col < 9; col++) {
                if((row == 0 || row == 8) && col != 0 && col != 8) { // letters
                    
                } else if((col == 0 || col == 8) && row != 0 && row != 8) { // numbers

                } else {
                    
                }
            }
        }
    }
}
