package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    private String color;

    public ConnectCommand(String authToken, Integer gameID, String color) {
        super(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        this.color = color;
    }

    public String getColor() { return color; }
}
