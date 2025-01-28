package service.handlers;
import com.google.gson.Gson;
import model.Task;
import model.enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHandlerTest {

    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 4, 3, 9, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 4, 3, 10, 0);
        Duration duration = Duration.ofMinutes(30);

        Task task1 = new Task(1, "High Priority Task", "Task Description 1", Status.NEW, startTime1, duration);
        Task task2 = new Task(2, "Low Priority Task", "Task Description 2", Status.NEW, startTime2, duration);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Failed to fetch prioritized tasks");
        List<Task> prioritizedTasks = gson.fromJson(response.body(), new TaskTypeToken().getType());
        Assertions.assertNotNull("Prioritized tasks list should not be null", prioritizedTasks.toString());
        assertEquals(2, prioritizedTasks.size(), "Prioritized tasks list size is incorrect");
        assertEquals(task1.getId(), prioritizedTasks.get(0).getId(), "First task in prioritized list is incorrect");
        assertEquals(task2.getId(), prioritizedTasks.get(1).getId(), "Second task in prioritized list is incorrect");
    }
}
