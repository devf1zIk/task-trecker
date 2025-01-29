package service.handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import service.Managers;
import service.managers.TaskManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = Managers.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String request = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            switch (request) {
                case "GET":
                    if (Pattern.matches("/api/epics/\\d+/subtasks", path)) {
                        String repath = path.replaceFirst("/api/epics/", "").replaceFirst("/subtasks", "");
                        int id = parseInt(repath);
                        Epic epic = taskManager.getEpic(id);
                        if (epic != null) {
                            String response = gson.toJson(taskManager.getSubtasksOfEpic(id));
                            sendText(exchange, response);
                        } else {
                            sendNotFound(exchange);
                        }
                    } else if (Pattern.matches("/api/epics", path)) {
                        sendText(exchange, gson.toJson(taskManager.getAllEpics()));
                    } else if (Pattern.matches("/api/epics/\\d+", path)) {
                        int id = parseInt(path.replaceFirst("/api/epics/", ""));
                        Epic epic = taskManager.getEpic(id);
                        if (epic != null) {
                            sendText(exchange, gson.toJson(epic));
                        } else {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                    break;

                case "POST":
                    if (body.isBlank()) {
                        sendBadRequest(exchange);
                        return;
                    }
                    if (Pattern.matches("/api/epics", path)) {
                        try {
                            Epic epic = gson.fromJson(body, Epic.class);
                            if (epic == null) {
                                sendBadRequest(exchange);
                                return;
                            }
                            taskManager.addEpic(epic);
                            exchange.sendResponseHeaders(201, 0);
                        } catch (Exception e) {
                            sendInternalServerError(exchange, e.getMessage());
                        }
                    } else if (Pattern.matches("/api/epics/\\d+", path)) {
                        int id = parseInt(path.replaceFirst("/api/epics/", ""));
                        if (taskManager.getEpic(id) != null) {
                            try {
                                Epic epic = gson.fromJson(body, Epic.class);
                                if (epic == null || epic.getId() != id) {
                                    sendBadRequest(exchange);
                                    return;
                                }
                                taskManager.updateEpic(epic);
                                exchange.sendResponseHeaders(200, 0);
                            } catch (Exception e) {
                                sendBadRequest(exchange);
                            }
                        } else {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                    break;

                case "DELETE":
                    if (Pattern.matches("/api/epics", path)) {
                        taskManager.deleteAllEpics();
                        exchange.sendResponseHeaders(200, 0);
                    } else if (Pattern.matches("/api/epics/\\d+", path)) {
                        int id = parseInt(path.replaceFirst("/api/epics/", ""));
                        if (taskManager.getEpic(id) != null) {
                            taskManager.removeEpic(id);
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, 0);
            }
        }
    }
}
