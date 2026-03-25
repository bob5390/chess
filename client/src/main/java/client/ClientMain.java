package client;

public class ClientMain {
    private static final Repl CLIENT = new Repl("http://localhost:8080");

    public static void main(String[] args) {
        CLIENT.run();
    }
}
