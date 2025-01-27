package service.handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import service.Managers;
import service.managers.TaskManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = Managers.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange){
            String request = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (request) {
                case "GET":
                    if (Pattern.matches("/api/epics/\\d + /subtasks", path)) {
                        String repath = path.replaceFirst("/api/epics/", "").replaceFirst("/subtasks","");
                        int id = parseInt(repath);
                        if (taskManager.getEpic(id) != null) {
                            String response = gson.toJson(taskManager.getSubtasksOfEpic(id));
                            sendText(exchange, response);
                            return;
                        } else {
                            sendNotFound(exchange);
                            return;
                        }
                    } else if (Pattern.matches("/api/epics", path)) {
                        String response = gson.toJson(taskManager.getAllEpics());
                        sendText(exchange, response);
                        return;
                    } else if (Pattern.matches("/api/epics/\\d", path)) {
                        String repath = path.replaceFirst("/api/epics/", "");
                        int id = parseInt(repath);
                        if (taskManager.getEpic(id) != null) {
                            String response = gson.toJson(taskManager.getEpic(id));
                            sendText(exchange, response);
                            return;
                        } else {
                            sendNotFound(exchange);
                            return;
                        }
                    }
                    break;
                case "POST":
                    if (Pattern.matches("/api/epics/", path)) {
                        InputStream inputStream = exchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Epic epic = gson.fromJson(body, Epic.class);
                        taskManager.addEpic(epic);
                        exchange.sendResponseHeaders(201, 0);
                        return;
                    } else if (Pattern.matches("/api/epics/\\d", path)) {
                        String repath = path.replaceFirst("/api/epics/", "");
                        int id = parseInt(repath);
                        if (taskManager.getEpic(id) != null) {
                            InputStream inputStream = exchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Epic epic = gson.fromJson(body, Epic.class);
                            taskManager.updateEpic(epic);
                            exchange.sendResponseHeaders(201, 0);
                            return;
                        } else {
                        sendNotFound(exchange);
                        return;
                    }
                }
                    break;
                case "DELETE":
                    if (Pattern.matches("/api/epics", path)) {
                        taskManager.deleteAllEpics();
                        exchange.sendResponseHeaders(200, 0);
                        return;
                    } else if (Pattern.matches("/api/epics/\\d", path)) {
                        String repath = path.replaceFirst("/api/epics/", "");
                        int id = parseInt(repath);
                        if (taskManager.getEpic(id) != null) {
                            taskManager.removeEpic(id);
                            exchange.sendResponseHeaders(201, 0);
                            return;
                        } else {
                            sendNotFound(exchange);
                            return;
                        }
                    }
                    break;
                default:
                    System.out.println("Сервер с методами ошибка, такой метод нету" + request);
                    exchange.sendResponseHeaders(405, 0);
            }
        }
    }
}
