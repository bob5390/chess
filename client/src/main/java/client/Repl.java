package client;

import java.util.Scanner;

import ui.EscapeSequences;

public class Repl {
    private ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.print(EscapeSequences.SET_TEXT_BOLD);
        System.out.println(EscapeSequences.BLACK_PAWN 
                           + "Welcome to chess! Type `help` to see a list of valid commands." 
                           + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        Scanner scanner = new Scanner(System.in);

        String parsedInput = "";
        while(!parsedInput.equals(ChessClient.QUIT_MESSAGE)) {
            promptUser();
            String line = scanner.nextLine();
            parsedInput = client.eval(line.strip());

            if(parsedInput.contains("Error:")) {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
            } else {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
            }
            System.out.print(parsedInput + EscapeSequences.RESET_TEXT_COLOR);
        }
        scanner.close();
    }

    private void promptUser() {
        System.out.print("\n\n" + EscapeSequences.SET_TEXT_FAINT + client.curPrompt() + EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }
}
