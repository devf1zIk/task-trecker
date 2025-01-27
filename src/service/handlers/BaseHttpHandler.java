package service.handlers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    private final String notFound = "Not Found";
    private final String badRequest = "Bad Request";
    private final String notAccess = "Not Access";

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
        byte[] resp = badRequest.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(400, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
        System.out.println("Bad Request");
    }

    protected void sendNotFound(final HttpExchange exchange) throws IOException {
        byte[] resp = notFound.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(404, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
        System.out.println("Not Found");
    }

    protected void sendHasInteraction(final HttpExchange exchange) throws IOException {
        byte[] resp = notAccess.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(406, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
        System.out.println("Not Access");
    }

    protected Integer parseInt(final String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
