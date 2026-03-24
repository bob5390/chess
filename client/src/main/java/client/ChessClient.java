package client;

import java.util.Arrays;

import io.javalin.http.HttpResponseException;
import model.GameData;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.ListGamesResult;
import results.LoginResult;
import results.RegisterResult;
import server.ServerFacade;
import ui.EscapeSequences;

public class ChessClient {
    private final ServerFacade serverFacade;
    private enum State {LOGGED_OUT, LOGGED_IN};
    private State state = State.LOGGED_OUT;
    public static final String QUIT_MESSAGE = "Goodbye!";
    private String curAuthToken = "";

    public ChessClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    public String eval(String prompt) {
        try {
            String[] tokens = prompt.toLowerCase().split(" ");
            validateTokens(tokens);
            String command = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
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
                default -> throw new Exception("Error: Unknown Command");
            };
        } catch(Exception e) {
            return e.getMessage();
        }
    }

    public String curPrompt() {
        if(state == State.LOGGED_IN) {
            return "Chess >>> ";
        } else {
            return "Login/Register >>> ";
        }
    }

    private void validateTokens(String... tokens) throws Exception {
        if(tokens.length < 1
        || !tokens[0].equals("help")) {
            throw new Exception(String.format("Error: %s is not a valid command", String.join(" ", tokens)));
        }

        if(state == State.LOGGED_IN) {
            if(!tokens[0].equals("logout")
            || !tokens[0].equals("create")
            || !tokens[0].equals("list")
            || !tokens[0].equals("join")
            || !tokens[0].equals("observe")) {
                throw new Exception(String.format("Error: %s is not a valid command", String.join(" ", tokens)));
            }
        } else {
            if(!tokens[0].equals("quit")
            || !tokens[0].equals("login")
            || !tokens[0].equals("register")) {
                throw new Exception(String.format("Error: %s is not a valid command", String.join(" ", tokens)));
            }
        }
    }

    private String help() {
        if(state == State.LOGGED_IN) {
            return "";
        } else {
            return EscapeSequences.SET_TEXT_ITALIC 
                + "  help\n  login <username> <password>\n  register <username> <password> <email>\n  quit";
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
                return EscapeSequences.SET_TEXT_ITALIC + "Successfully logged in " + params[0];
            } catch(HttpResponseException e) {
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
                return EscapeSequences.SET_TEXT_ITALIC + "Successfully registered " + params[0];
            } catch(HttpResponseException e) {
                throw new Exception(String.format("Error: Couldn't register user `%s`", params[0]));
            }
        }
    }

    private String logout() throws Exception {
        try {
            serverFacade.logout(new LogoutRequest(curAuthToken));
            curAuthToken = "";
            return EscapeSequences.SET_TEXT_ITALIC + "Successfully logged out";
        } catch(HttpResponseException e) {
            throw new Exception("Error: Couldn't log out");
        }
    }

    private String create(String... params) throws Exception {
        if(params.length != 1) {
            throw new Exception("Error: Invalid number of arguments - Usage: create <game name>");
        } else {
            try {
                serverFacade.createGame(new CreateGameRequest(curAuthToken, params[1]));
                return EscapeSequences.SET_TEXT_ITALIC + "Successfully created game " + params[0];
            } catch(HttpResponseException e) {
                throw new Exception(String.format("Error: Couldn't create game `%s`", params[0]));
            }
        }
    }

    private String list() throws Exception {
        try {
            ListGamesResult list = serverFacade.listGames(new ListGamesRequest(curAuthToken));
            return list.toString();
        } catch(HttpResponseException e) {
            throw new Exception("Error: Couldn't list games");
        }
    }

    private String join(String... params) throws Exception {
        if(params.length != 1 || params.length != 2) {
            throw new Exception("Error: Invalid number of arguments - Usage: join <ID> [WHITE|BLACK]");
        } else {
            try {
                // randomize player color if needed
                ListGamesResult list = serverFacade.listGames(new ListGamesRequest(curAuthToken));
                GameData game = list.getGameList().get(Integer.parseInt(params[0]));
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
                if(params.length == 2) color = params[1];

                serverFacade.joinGame(new JoinGameRequest(curAuthToken, color, params[0]));
                return EscapeSequences.SET_TEXT_ITALIC + "Successfully joined game";
            } catch(HttpResponseException e) {
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
                GameData game = list.getGameList().get(Integer.parseInt(params[0]));
                return EscapeSequences.SET_TEXT_ITALIC + "Observing game";
            } catch(HttpResponseException e) {
                throw new Exception("Error: Couldn't observe game");
            }
        }
    }
}
