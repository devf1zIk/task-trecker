import service.Managers;
import service.server.HttpTaskServer;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
        server.start();
        System.out.println("Server started");
    }
}
