package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.JoinGameResult;
import results.ListGamesResult;
import results.LoginResult;
import results.RegisterResult;
import server.ServerFacade;
import server.ServerMessageObserver;
import server.WebSocketFacade;
import ui.EscapeSequences;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class ChessClient implements ServerMessageObserver {
    private final ServerFacade serverFacade;
    private final WebSocketFacade webSocketFacade;
    private enum State {LOGGED_OUT, LOGGED_IN, IN_GAME, CONFIRM, OBSERVING};
    private State state = State.LOGGED_OUT;
    public static final String QUIT_MESSAGE = "Goodbye!";
    private String curAuthToken = "";
    private GameData currentGame = null;
    private BoardDrawer boardDrawer = new BoardDrawer();
    private String currentColor = "";
    private String toConfirm = "";
    private String serverUrl = "";

    public ChessClient(String serverUrl) throws Exception {
        this.serverUrl = serverUrl;
        serverFacade = new ServerFacade(serverUrl);
        webSocketFacade = new WebSocketFacade();
    }

    public String eval(String prompt) {
        try {
            String[] tokens = prompt.toLowerCase().split(" ");
            validateTokens(tokens);
            String command = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if(state == State.CONFIRM) { 
                evalConfirm(command);
                return "";
            }
            if(command.equals("quit")) { return quit(); }
            return switch (command) {
                case "help" -> help();
                case "quit" -> quit();
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "leave" -> leave();
                case "move" -> move(params);
                case "redraw" -> redraw();
                case "resign" -> resign();
                case "highlight" -> highlightMoves(params);
                default -> throw new Exception("Error: Unknown Command");
            };
        } catch(Exception e) {
            return e.getMessage();
        }
    }

    public String curPrompt() {
        if(state == State.IN_GAME) {
            redraw();
        }

        if(state == State.LOGGED_OUT) {
            return "Login/Register >>> ";
        } else if(state == State.CONFIRM) {
            return "Are you sure (yes[y]/no[n])? >>> ";
        } else {
            return "Chess >>> ";
        }
    }

    private void validateTokens(String... tokens) throws Exception {
        if(!tokens[0].equals("help") && !tokens[0].equals("quit")) {
            if(state == State.LOGGED_IN) {
                if(!tokens[0].equals("logout")
                && !tokens[0].equals("create")
                && !tokens[0].equals("list")
                && !tokens[0].equals("join")
                && !tokens[0].equals("observe")) {
                    throw new Exception(String.format("Error: %s is not a valid command", String.join(" ", tokens)));
                }
            } else if(state == State.IN_GAME || state == State.OBSERVING) {
                if(!tokens[0].equals("leave")
                && !tokens[0].equals("resign")
                && !tokens[0].equals("move")
                && !tokens[0].equals("highlight")
                && !tokens[0].equals("redraw")) {
                    throw new Exception(String.format("Error: %s is not a valid command", String.join(" ", tokens)));
                }
            } else if(state == State.CONFIRM) {
                if(!tokens[0].equals("y")
                && !tokens[0].equals("yes")
                && !tokens[0].equals("n")
                && !tokens[0].equals("no")) {
                    throw new Exception(String.format("Error: %s is not a valid command", String.join(" ", tokens)));
                }
            } else {
                if(!tokens[0].equals("login")
                && !tokens[0].equals("register")) {
                    throw new Exception(String.format("Error: %s is not a valid command", String.join(" ", tokens)));
                }
            }
        }
    }

    private String help() {
        if(state == State.LOGGED_IN) {
            return EscapeSequences.SET_TEXT_ITALIC
                + "  create <game name>\n  help\n  join <ID> [WHITE|BLACK]\n  list\n  logout  \n  observe <ID>\n  quit"
                + EscapeSequences.RESET_TEXT_ITALIC;
        } else if(state == State.IN_GAME) {
            return EscapeSequences.SET_TEXT_ITALIC
                + "  help\n  highlight <square>\n  leave\n  move <from> <to>\n  redraw\n  resign"
                + EscapeSequences.RESET_TEXT_ITALIC;
        } else if(state == State.CONFIRM) {
            return EscapeSequences.SET_TEXT_ITALIC
                + "  help\n  yes|y\n  no|n"
                + EscapeSequences.RESET_TEXT_ITALIC;
        } else if(state == State.OBSERVING) {
            return EscapeSequences.SET_TEXT_ITALIC
                + "  help\n  highlight <square>\n  leave\n  redraw"
                + EscapeSequences.RESET_TEXT_ITALIC;
        } else {
            return EscapeSequences.SET_TEXT_ITALIC 
                + "  help\n  login <username> <password>\n  register <username> <password> <email>\n  quit"
                + EscapeSequences.RESET_TEXT_ITALIC;
        }
    }

    private String quit() {
        return QUIT_MESSAGE;
    }

    private String login(String... params) throws Exception {
        if(params.length != 2) {
            throw new Exception("Error: Invalid number of arguments - Usage: login <username> <password>");
        } else {
            try {
                LoginResult login = serverFacade.login(new LoginRequest(params[0], params[1]));
                curAuthToken = login.getAuthToken();
                state = State.LOGGED_IN;
                return EscapeSequences.SET_TEXT_ITALIC + "Successfully logged in " + params[0] + EscapeSequences.RESET_TEXT_ITALIC;
            } catch(Exception e) {
                throw new Exception(String.format("Error: Couldn't log in user `%s`", params[0]));
            }
        }
    }

    private String register(String... params) throws Exception {
        if(params.length < 3) {
            throw new Exception("Error: Invalid number of arguments - Usage: register <username> <password> <email>");
        } else {
            try {
                RegisterResult register = serverFacade.register(new RegisterRequest(params[0], params[1], params[2]));
                curAuthToken = register.getAuthToken();
                state = State.LOGGED_IN;
                return EscapeSequences.SET_TEXT_ITALIC + "Successfully registered " + params[0] + EscapeSequences.RESET_TEXT_ITALIC;
            } catch(Exception e) {
                throw new Exception(String.format("Error: Couldn't register user `%s`", params[0]));
            }
        }
    }

    private String logout() throws Exception {
        try {
            serverFacade.logout(new LogoutRequest(curAuthToken));
            curAuthToken = "";
            state = State.LOGGED_OUT;
            return EscapeSequences.SET_TEXT_ITALIC + "Successfully logged out" + EscapeSequences.RESET_TEXT_ITALIC;
        } catch(Exception e) {
            throw new Exception("Error: Couldn't log out");
        }
    }

    private String create(String... params) throws Exception {
        if(params.length != 1) {
            throw new Exception("Error: Invalid number of arguments - Usage: create <game name>");
        } else {
            try {
                serverFacade.createGame(new CreateGameRequest(curAuthToken, params[0]));
                return EscapeSequences.SET_TEXT_ITALIC + "Successfully created game " + params[0] + EscapeSequences.RESET_TEXT_ITALIC;
            } catch(Exception e) {
                throw new Exception(String.format("Error: Couldn't create game `%s`", params[0]));
            }
        }
    }

    private String list() throws Exception {
        try {
            ListGamesResult list = serverFacade.listGames(new ListGamesRequest(curAuthToken));
            return list.toString();
        } catch(Exception e) {
            throw new Exception("Error: Couldn't list games");
        }
    }

    private String join(String... params) throws Exception {
        if(params.length != 1 && params.length != 2) {
            throw new Exception("Error: Invalid number of arguments - Usage: join <ID> [WHITE|BLACK]");
        } else {
            try {
                // randomize player color if needed
                ListGamesResult list = serverFacade.listGames(new ListGamesRequest(curAuthToken));
                GameData game = list.getGameList().get(Integer.parseInt(params[0])-1);
                String color = null;
                if(game.getWhiteUsername() != null && !game.getWhiteUsername().equals("")) {
                    if(game.getBlackUsername() != null && !game.getBlackUsername().equals("")) {
                        throw new Exception("Error: Game already full");
                    } else {
                        color = "BLACK";
                    }
                } else {
                    color = "WHITE";
                }
                if(params.length == 2) { color = params[1].toUpperCase(); }

                JoinGameResult join = serverFacade.joinGame(new JoinGameRequest(curAuthToken, color, params[0]));
                currentGame = join.getGameData();
                currentColor = color;
                webSocketFacade.connect(serverUrl, this, curAuthToken, color.toLowerCase(), Integer.parseInt(params[0]));
                state = State.IN_GAME;
                return EscapeSequences.SET_TEXT_ITALIC + "Successfully joined game" + EscapeSequences.RESET_TEXT_ITALIC;
            } catch(Exception e) {
                throw new Exception("Error: Couldn't join game");
            }
        }
    }

    private String observe(String... params) throws Exception {
        if(params.length != 1) {
            throw new Exception("Error: Invalid number of arguments - Usage: observe <ID>");
        } else {
            try {
                ListGamesResult list = serverFacade.listGames(new ListGamesRequest(curAuthToken));
                GameData game = list.getGameList().get(Integer.parseInt(params[0])-1);
                currentGame = game;
                currentColor = "WHITE";
                webSocketFacade.connect(serverUrl, this, curAuthToken, "observer", Integer.parseInt(params[0]));
                state = State.OBSERVING;
                return EscapeSequences.SET_TEXT_ITALIC + "Observing game" + EscapeSequences.RESET_TEXT_ITALIC;
            } catch(Exception e) {
                throw new Exception("Error: Couldn't observe game");
            }
        }
    }

    private String leave() throws Exception {
        webSocketFacade.leave(curAuthToken, Integer.parseInt(currentGame.getGameID()));

        currentColor = "";
        currentGame = null;
        state = State.LOGGED_IN;
        return EscapeSequences.SET_TEXT_ITALIC + "Left game" + EscapeSequences.RESET_TEXT_ITALIC;
    }

    private String move(String... params) throws Exception {
        if(params.length != 2) {
            throw new Exception("Error: Invalid number of arguments - Usage: move <from space> <to space>");
        } else {
            try {
                ChessGame game = currentGame.getChessGame();
                ChessMove moveToMake = parseMove(params[0], params[1], game);
                game.makeMove(moveToMake);
                webSocketFacade.makeMove(moveToMake);
                return EscapeSequences.SET_TEXT_ITALIC + "Successfully made move" + EscapeSequences.RESET_TEXT_ITALIC;
            } catch(Exception e) {
                throw new Exception("Error: Couldn't make move");
            }
        }
    }

    private String redraw() {
        System.out.println(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        boardDrawer.setBoard(currentGame.getChessGame().getBoard());
        boardDrawer.drawBoard(currentColor);
        return "";
    }

    private String redraw(Collection<ChessMove> toHighlight) {
        System.out.println(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        boardDrawer.setBoard(currentGame.getChessGame().getBoard());
        
        Collection<ChessPosition> positions = new ArrayList<ChessPosition>();
        for(ChessMove m : toHighlight) {
            positions.add(m.getEndPosition());
        }

        boardDrawer.drawBoard(currentColor, positions);
        return "";
    }

    private String resign() {
        state = State.CONFIRM;
        toConfirm = "resign";
        return "";
    }

    private String highlightMoves(String... params) throws Exception {
        if(params.length != 1) {
            throw new Exception("Error: Invalid number of arguments - Usage: highlight <square>");
        }
        ChessPosition targetSquare = parsePosition(params[0]);
        Collection<ChessMove> potentialMoves = currentGame.getChessGame().validMoves(targetSquare);
        return redraw(potentialMoves);
    }

    private ChessMove parseMove(String from, String to, ChessGame game) throws Exception {
        ChessPosition startPosition = parsePosition(from);
        ChessPosition endPosition = parsePosition(to);
        ChessPiece targetPiece = game.getBoard().getPiece(startPosition);
        Collection<ChessMove> validMoves = targetPiece.pieceMoves(game.getBoard(), startPosition);
        for(ChessMove toTest : validMoves) {
            if(toTest.getStartPosition().equals(startPosition) && toTest.getEndPosition().equals(endPosition)) {
                return toTest;
            }
        }
        return null;
    }

    private ChessPosition parsePosition(String pos) throws Exception {
        if(pos.length() == 2 && pos.charAt(0) <= 'h' && pos.charAt(0) >= 'a' && pos.charAt(1) <= '8' && pos.charAt(1) >= '1') {
            int col = (int)(pos.charAt(0) - 'a') + 1;
            int row = Integer.parseInt(pos.substring(1));
            return new ChessPosition(row, col);
        } else {
            throw new Exception("Error: invalid position format");
        }
    }

    private void evalConfirm(String command) throws NumberFormatException, IOException {
        if(command.contains("y")) {
            if(toConfirm.equals("resign")) {
                webSocketFacade.resign(curAuthToken, Integer.parseInt(currentGame.getGameID()));
            }
        } else {
            state = State.IN_GAME;
        }
    }

    private void displayNotification(String message) {
        System.out.println(EscapeSequences.SET_TEXT_ITALIC + message + EscapeSequences.RESET_TEXT_ITALIC);
        curPrompt();
    }

    private void displayError(String errorMessage) {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.SET_TEXT_BOLD + errorMessage +
                         EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        curPrompt();
    }

    private void loadGame(ChessGame toLoad) {
        currentGame.setGame(toLoad);
        if(state != State.IN_GAME) { redraw(); }
        curPrompt();
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage)message).getErrorMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage)message).getGame());
        }
    }
}
