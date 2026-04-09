package client;

public class ClientMain {
    public static void main(String[] args) {
        try {
            new Repl("http://localhost:8080").run();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("Unable to start server: %s\n", e.getMessage());
        }
    }
}
