package service.handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.Managers;
import service.managers.TaskManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager) {
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
                    if (Pattern.matches("/api/tasks$", path)) {
                        String response = gson.toJson(taskManager.getAllTasks());
                        sendText(exchange, response);
                        return;
                    } else if (Pattern.matches("/api/tasks/\\d+$", path)) {
                        String repath = path.replaceFirst("/api/tasks/", "");
                        int id = parseInt(repath);
                        if (taskManager.getTask(id) != null) {
                            String response = gson.toJson(taskManager.getTask(id));
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
                    if (Pattern.matches("/api/tasks$", path)) {
                        if (body.isBlank()) {
                            sendBadRequest(exchange);
                            return;
                        }
                        Task task = gson.fromJson(body, Task.class);
                        if (task == null) {
                            sendBadRequest(exchange);
                            return;
                        }
                        taskManager.addTask(task);
                        exchange.sendResponseHeaders(201,0);
                        return;
                    } else if (Pattern.matches("/api/tasks/\\d+$", path)) {
                        String repath = path.replaceFirst("/api/tasks/", "");
                        int id = parseInt(repath);
                        if (taskManager.getTask(id) != null) {
                            if (body.isBlank()) {
                                sendBadRequest(exchange);
                                return;
                            }
                            Task task = gson.fromJson(body, Task.class);
                            if (task == null) {
                                sendBadRequest(exchange);
                                return;
                            }
                            taskManager.updateTask(task);
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
                    if (Pattern.matches("/api/tasks/\\d", path)) {
                        String repath = path.replaceFirst("/api/tasks/", "");
                        int id = parseInt(repath);
                        if (taskManager.getTask(id) != null) {
                            taskManager.removeTask(id);
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
