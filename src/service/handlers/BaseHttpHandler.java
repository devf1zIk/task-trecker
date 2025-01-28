package service.handlers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    private static final String NOT_FOUND = "Not Found";
    private static final String BAD_REQUEST = "Bad Request";

    protected void sendText(final HttpExchange exchange, final String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
        System.out.println("Successfully sent text");
    }

    protected void sendInternalServerError(final HttpExchange exchange, final String errorMessage) throws IOException {
        byte[] resp = errorMessage.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(500, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
        System.out.println("Internal Server Error");
    }

    protected void sendBadRequest(final HttpExchange exchange) throws IOException {
        byte[] resp = BAD_REQUEST.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(400, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
        System.out.println("Bad Request");
    }

    protected void sendNotFound(final HttpExchange exchange) throws IOException {
        byte[] resp = NOT_FOUND.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(404, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
        System.out.println("Not Found");
    }

    protected Integer parseInt(final String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
