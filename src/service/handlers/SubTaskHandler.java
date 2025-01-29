package service.handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.SubTask;
import service.Managers;
import service.managers.TaskManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SubTaskHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public SubTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = Managers.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String request = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            switch (request) {
                case "GET":
                    if (Pattern.matches("/api/subtasks", path)) {
                        String response = gson.toJson(taskManager.getAllSubtasks());
                        sendText(exchange, response);
                        return;
                    } else if (Pattern.matches("/api/subtasks/\\d+$", path)) {
                        String repath = path.replaceFirst("/api/subtasks/", "");
                        int id = parseInt(repath);
                        if (taskManager.getSubtask(id) != null) {
                            String response = gson.toJson(taskManager.getSubtask(id));
                            sendText(exchange, response);
                            return;
                        } else {
                            sendNotFound(exchange);
                            return;
                        }

                    } else {
                        sendNotFound(exchange);
                        return;
                    }
                case "POST":
                    if (body.isBlank()) {
                        sendBadRequest(exchange);
                        return;
                    }
                    if (Pattern.matches("/api/subtasks", path)) {
                        SubTask subTask = gson.fromJson(body, SubTask.class);
                        taskManager.addSubtask(subTask);
                        exchange.sendResponseHeaders(201,0);
                        return;
                    } else if (Pattern.matches("/api/subtasks/\\d + $", path)) {
                        String repath = path.replaceFirst("/api/subtasks/", "");
                        int id = parseInt(repath);
                        if (taskManager.getSubtask(id) != null) {
                            if (body.isBlank()) {
                                sendBadRequest(exchange);
                                return;
                            }
                            SubTask subTask = gson.fromJson(body, SubTask.class);
                            taskManager.updateSubtask(subTask);
                            exchange.sendResponseHeaders(201,0);
                            return;
                        }  else {
                            sendNotFound(exchange);
                            return;
                        }
                    } else {
                        sendNotFound(exchange);
                        return;
                    }
                case "DELETE":
                    if (Pattern.matches("/api/subtasks", path)) {
                        taskManager.deleteAllSubtasks();
                        exchange.sendResponseHeaders(200, 0);
                    } else if (Pattern.matches("/api/subtasks/\\d+", path)) {
                        int id = parseInt(path.replaceFirst("/api/subtasks/", ""));
                        SubTask subTask = taskManager.getSubtask(id);
                        if (subTask != null) {
                            taskManager.removeSubtask(id);
                            exchange.sendResponseHeaders(200, 0);
                            return;
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                default:
                    System.out.println("Сервер с методами ошибка, такой метод нету" + request);
                    exchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            String response = e.getMessage();
            sendInternalServerError(exchange,response);
        }
    }
}
