package service.handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.SubTask;
import service.Managers;
import service.managers.TaskManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {

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
                    if (Pattern.matches("/api/subtasks", path)) {
                        InputStream inputStream = exchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        SubTask subTask = gson.fromJson(body, SubTask.class);
                        taskManager.addSubtask(subTask);
                        exchange.sendResponseHeaders(201,0);
                        return;
                    } else if (Pattern.matches("/api/subtasks/\\d + $", path)) {
                        String repath = path.replaceFirst("/api/subtasks/", "");
                        int id = parseInt(repath);
                        if (taskManager.getSubtask(id) != null) {
                            InputStream inputStream = exchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
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
                    if (Pattern.matches("/api/subtasks/\\d", path)) {
                        String repath = path.replaceFirst("/api/subtasks/", "");
                        int id = parseInt(repath);
                        if (taskManager.getSubtask(id) != null) {
                            taskManager.removeSubtask(id);
                            exchange.sendResponseHeaders(200,0);
                            return;
                        } else {
                            sendNotFound(exchange);
                            return;
                        }
                    } else {
                        sendNotFound(exchange);
                        return;
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
