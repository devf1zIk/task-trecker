package service.handlers;
import com.google.gson.Gson;
import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.adapters.TaskTypeToken;
import service.managers.InMemoryTaskManager;
import service.managers.TaskManager;
import service.server.HttpTaskServer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class HistoryHandlerTest {

    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        gson = Managers.getGson();
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testHistory() throws IOException, InterruptedException {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        Task task2 = new Task(2, "Task 2", "Description 2", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(10));
        Epic epic = new Epic(3, "Epic 1", "Epic Description", Status.NEW, LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(30));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);

        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getEpic(3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Failed to fetch history");
        assertNotNull(response.body(), "Response body is null");

        List<Task> history = gson.fromJson(response.body(), new TaskTypeToken().getType());
        assertNotNull(history, "History should not be null");

        assertEquals(3, history.size(), "Incorrect number of tasks in history");
        assertEquals(1, history.get(0).getId(), "First task in history is incorrect");
        assertEquals(2, history.get(1).getId(), "Second task in history is incorrect");
        assertEquals(3, history.get(2).getId(), "Third task in history is incorrect");
    }

    @Test
    public void testEmptyHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Failed to fetch history");
        assertNotNull(response.body(), "Response body is null");

        List<Task> history = gson.fromJson(response.body(), new TaskTypeToken().getType());
        assertNotNull(history, "History should not be null");
        assertTrue(history.isEmpty(), "History should be empty");
    }

    @Test
    public void testTaskRemovedFromHistory() throws IOException, InterruptedException {
        Task task = new Task(1, "Task 1", "Description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        taskManager.addTask(task);

        taskManager.getTask(1);
        taskManager.removeTask(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Failed to fetch history");
        assertNotNull(response.body(), "Response body is null");

        List<Task> history = gson.fromJson(response.body(), new TaskTypeToken().getType());
        assertNotNull(history, "History should not be null");
        assertTrue(history.isEmpty(), "History should be empty after task removal");
    }

    @Test
    public void testHistoryWithEpicsAndSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic 1", "Epic Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        SubTask subTask = new SubTask(2, "Subtask 1", "Subtask Description", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(10), epic.getId());

        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask);

        taskManager.getEpic(1);
        taskManager.getSubtask(2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Failed to fetch history");
        assertNotNull(response.body(), "Response body is null");

        List<Task> history = gson.fromJson(response.body(), new TaskTypeToken().getType());
        assertNotNull(history, "History should not be null");
        assertEquals(2, history.size(), "Incorrect number of tasks in history");
        assertEquals(1, history.get(0).getId(), "First item in history is not the epic");
        assertEquals(2, history.get(1).getId(), "Second item in history is not the subtask");
    }

    @Test
    public void testHistoryClearedAfterDeletingAllTasks() throws IOException, InterruptedException {
        Task task = new Task(1, "Task 1", "Description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        taskManager.addTask(task);
        taskManager.getTask(1);

        taskManager.deleteAllTasks();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Failed to fetch history");
        assertNotNull(response.body(), "Response body is null");

        List<Task> history = gson.fromJson(response.body(), new TaskTypeToken().getType());
        assertNotNull(history, "History should not be null");
        assertTrue(history.isEmpty(), "History should be empty after deleting all tasks");
    }
}
