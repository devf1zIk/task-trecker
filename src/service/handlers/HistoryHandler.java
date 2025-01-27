package service.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.Managers;
import service.managers.TaskManager;
import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = Managers.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String request = exchange.getRequestMethod();
            switch (request){
                case "GET":
                    String path = exchange.getRequestURI().getPath();
                    if (path.equals("/api/history")) {
                        String response = gson.toJson(taskManager.getHistory());
                        sendText(exchange,response);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                default:
                    System.out.println("Сервер с методами ошибка, такой метод нету" + request);
                    exchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            String response = e.getMessage();
            sendInternalServerError(exchange, response);
        }
    }
}
