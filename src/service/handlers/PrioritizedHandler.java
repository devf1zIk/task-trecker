package service.handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.Managers;
import service.managers.TaskManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            String path = exchange.getRequestURI().getPath();
            if ("GET".equals(request)) {
                if ("/api/prioritized".equals(path)) {
                    List<Task> prioritizedTasks = taskManager.getPriorityTasks();
                    String response = gson.toJson(prioritizedTasks != null ? prioritizedTasks : new ArrayList<>());
                    sendText(exchange, response);
                    return;
                }
                sendNotFound(exchange);
                return;
            }
            exchange.sendResponseHeaders(405, 0);
        } catch (Exception e) {
            sendInternalServerError(exchange, e.getMessage());
        }
    }
}
