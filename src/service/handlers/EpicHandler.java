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
                        if (taskManager.getEpic(id) != null) {
                            String response = gson.toJson(taskManager.getSubtasksOfEpic(id));
                            sendText(exchange, response);
                        } else {
                            sendNotFound(exchange);
                        }
                    } else if ("/api/epics".equals(path)) {
                        String response = gson.toJson(taskManager.getAllEpics());
                        sendText(exchange, response);
                    } else if (Pattern.matches("/api/epics/\\d+", path)) {
                        String repath = path.replaceFirst("/api/epics/", "");
                        int id = parseInt(repath);
                        if (taskManager.getEpic(id) != null) {
                            String response = gson.toJson(taskManager.getEpic(id));
                            sendText(exchange, response);
                        } else {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    if (Pattern.matches("/api/epics", path)) {
                        if (body.isBlank()) {
                            sendBadRequest(exchange);
                            return;
                        }
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
                        String repath = path.replaceFirst("/api/epics/", "");
                        int id = parseInt(repath);
                        if (taskManager.getEpic(id) != null) {
                            if (body.isBlank()) {
                                sendBadRequest(exchange);
                                return;
                            }
                            try {
                                Epic epic = gson.fromJson(body, Epic.class);
                                if (epic.getId() != id) {
                                    sendBadRequest(exchange);
                                    return;
                                }
                                if (taskManager.getEpic(id) != null) {
                                    taskManager.updateEpic(epic);
                                }
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
                    if ("/api/epics".equals(path)) {
                        taskManager.deleteAllEpics();
                        exchange.sendResponseHeaders(200, 0);
                    } else if (Pattern.matches("/api/epics/\\d+", path)) {
                        String repath = path.replaceFirst("/api/epics/", "");
                        int id = parseInt(repath);
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
