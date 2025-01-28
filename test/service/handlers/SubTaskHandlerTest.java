package service.handlers;
import com.google.gson.Gson;
import model.Epic;
import model.SubTask;
import model.enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.adapters.SubTaskTypeToken;
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

public class SubTaskHandlerTest {
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
    public void testAddSubTask() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2023, 4, 3, 5, 6);
        Duration duration = Duration.ofMinutes(5);
        LocalDateTime endTime = startTime.plus(duration);
        Epic epic = new Epic(3, "epic2", "descriepic2", Status.NEW, startTime, duration, endTime);
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask(1, "Subtask 1", "Description", Status.IN_PROGRESS, startTime, duration, epic.getId());
        String subTaskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Subtask was not added successfully");

        List<SubTask> subtasks = taskManager.getAllSubtasks();
        assertEquals(1, subtasks.size(), "Subtasks list size is incorrect");
        assertEquals("Subtask 1", subtasks.get(0).getName(), "Subtask name is incorrect");
    }

    @Test
    public void testGetSubTaskById() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2016, 3, 5, 5, 6);
        Duration duration = Duration.ofMinutes(3);
        LocalDateTime endTime = startTime.plus(duration);
        Epic epic = new Epic(1, "Epic 1", "Epic Description", Status.NEW, startTime, duration, endTime);
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask(1, "Subtask 1", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15), epic.getId());
        taskManager.addSubtask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Failed to fetch subtask by ID");

        SubTask returnedSubTask = gson.fromJson(response.body(), SubTask.class);
        assertEquals("Subtask 1", returnedSubTask.getName(), "Subtask name is incorrect");
    }

    @Test
    public void testGetAllSubTasks() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2014, 3, 5, 6, 1);
        Duration duration = Duration.ofMinutes(4);
        LocalDateTime endTime = startTime.plus(duration);
        Epic epic = new Epic(1, "Epic 1", "Epic Description", Status.NEW, startTime, duration, endTime);
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask(1, "Subtask 1", "Description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15), epic.getId());
        SubTask subTask2 = new SubTask(2, "Subtask 2", "Description 2", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(10), epic.getId());
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Failed to fetch all subtasks");
        List<SubTask> subtasks = gson.fromJson(response.body(), new SubTaskTypeToken().getType());
        assertEquals(2, subtasks.size(), "Incorrect number of subtasks");
    }
}
