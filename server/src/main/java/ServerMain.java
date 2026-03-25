
import server.Server;

public class ServerMain {
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}