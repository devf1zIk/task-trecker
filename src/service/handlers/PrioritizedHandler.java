package service.handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import service.Managers;
import service.managers.TaskManager;
import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = Managers.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String request = exchange.getRequestMethod();
            switch (request) {
                case "GET":
                    String path = exchange.getRequestURI().getPath();
                    if (path.equals("/api/prioritized")) {
                        String response = gson.toJson(taskManager.getPriorityTasks());
                        sendText(exchange, response);
                    }
                    break;
                default:
                    System.out.println("Invalid request");
                    sendBadRequest(exchange);
            }

        } catch (Exception e) {
            String response = e.getMessage();
            sendInternalServerError(exchange, response);
        }
    }
}
