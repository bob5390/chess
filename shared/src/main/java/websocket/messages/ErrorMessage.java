package websocket.messages;

public class ErrorMessage extends ServerMessage {
    String errorMessage;

    public ErrorMessage() {
        super(ServerMessageType.ERROR);
    }
}
