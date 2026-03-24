package client;

public class ClientMain {
    private static final Repl client = new Repl("http://localhost:8080");

    public static void main(String[] args) {
        client.run();
    }
}
