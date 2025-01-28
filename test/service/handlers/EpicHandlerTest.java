package service.handlers;
import com.google.gson.Gson;
import model.Epic;
import model.SubTask;
import model.enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.adapters.EpicTypeToken;
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

public class EpicHandlerTest {

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
    public void testAddEpic() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2021,3,4,5,5,6);
        Duration duration = Duration.ofMinutes(10);
        LocalDateTime endTime = startTime.plus(duration);
        Epic epic = new Epic(1, "Epic 1", "Test epic description", Status.NEW, startTime, duration, endTime);

        String epicJson = gson.toJson(epic);
        assertNotNull(epicJson, "Serialized epic JSON is null");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertNotNull(response, "Response is null");
        assertEquals(201, response.statusCode(), "Epic was not added successfully");

        List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Epics list is null");
        assertEquals(1, epics.size(), "Epics list size is incorrect");

        Epic returnedEpic = epics.get(0);
        assertEquals("Epic 1", returnedEpic.getName(), "Epic name is incorrect");
        assertEquals("Test epic description", returnedEpic.getDescription(), "Epic description is incorrect");
        assertEquals(startTime, returnedEpic.getStartTime(), "Epic start time is incorrect");
        assertEquals(duration, returnedEpic.getDuration(), "Epic duration is incorrect");
        assertEquals(endTime, returnedEpic.getEndTime(), "Epic end time is incorrect");
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(10);
        LocalDateTime endTime = startTime.plus(duration);
        Epic epic1 = new Epic(3,"Epic 1", "Description 1",Status.NEW,startTime,duration,endTime);
        Epic epic2 = new Epic(4,"Epic 2", "Description 2",Status.NEW,startTime,duration,endTime);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Failed to fetch all epics");

        List<Epic> epics = gson.fromJson(response.body(), new EpicTypeToken().getType());
        assertNotNull(epics, "Epics list is null");
        assertEquals(2, epics.size(), "Incorrect number of epics");
        assertEquals("Epic 1", epics.get(0).getName(), "First epic name is incorrect");
        assertEquals("Epic 2", epics.get(1).getName(), "Second epic name is incorrect");
    }

    @Test
    public void testCreateEpicWithEmptyBody() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Server did not return Bad Request for empty body");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2022, 2, 2, 14, 30);
        Duration duration = Duration.ofMinutes(15);
        LocalDateTime endTime = startTime.plus(duration);
        Epic epic = new Epic(1, "Epic Test", "Epic Description", Status.NEW, startTime, duration, endTime);
        taskManager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Failed to fetch epic by ID");

        Epic returnedEpic = gson.fromJson(response.body(), Epic.class);
        assertNotNull(returnedEpic, "Epic is null");
        assertEquals(epic.getId(), returnedEpic.getId(), "Epic ID is incorrect");
        assertEquals(epic.getName(), returnedEpic.getName(), "Epic name is incorrect");
    }

    @Test
    public void testDeleteAllEpics() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2020, 4, 5, 22, 3);
        Duration duration = Duration.ofMinutes(4);
        LocalDateTime endTime = startTime.plus(duration);
        Epic epic1 = new Epic(1, "Epic 1", "Description 1", Status.NEW, startTime,duration,endTime);
        Epic epic2 = new Epic(2, "Epic 2", "Description 2", Status.NEW, startTime,duration,endTime);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Failed to delete all epics");

        List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Epics list should not be null");
        assertTrue(epics.isEmpty(), "Epics list should be empty after deletion");
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2021, 5, 3, 19, 7);
        Duration duration = Duration.ofMinutes(8);
        LocalDateTime endTime = startTime.plus(duration);
        Epic epic1 = new Epic(1, "Epic 1", "Description 1", Status.NEW, startTime,duration,endTime);
        Epic epic2 = new Epic(2, "Epic 2", "Description 2", Status.NEW, startTime,duration,endTime);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Failed to delete epic by ID");

        List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Epics list should not be null");
        assertEquals(1, epics.size(), "Incorrect number of epics after deletion");
        assertEquals("Epic 2", epics.get(0).getName(), "Remaining epic is incorrect");
    }

    @Test
    public void testGetSubtasksOfEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(9, "Epic 9", "Description 9", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.addEpic(epic);

        SubTask subtask1 = new SubTask(2, "Subtask 1", "Subtask Description 1", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(5), 1);
        SubTask subtask2 = new SubTask(3, "Subtask 2", "Subtask Description 2", Status.DONE, LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(10), 1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Failed to fetch subtasks of epic");

        List<SubTask> subtasks = gson.fromJson(response.body(), new SubTaskTypeToken().getType());
        assertNotNull(subtasks, "Subtasks list is null");
        assertEquals(2, subtasks.size(), "Incorrect number of subtasks returned");
    }
}
