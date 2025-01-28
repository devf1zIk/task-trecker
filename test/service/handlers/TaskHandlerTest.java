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

public class TaskHandlerTest {

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
    public void testCreateTask() throws IOException, InterruptedException {

        Task task = new Task(1, "Spring boot 1", "Учим Bean",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<Task> tasks = taskManager.getAllTasks();
        System.out.println(tasks);

        Assertions.assertNotNull(tasks, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasks.size());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task existingTask = new Task(1, "Spring Boot", "Учим Bean", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(existingTask);

        Task updatedTask = new Task(1, "Spring boot", "Учим Component",
                Status.NEW, LocalDateTime.now().plusMinutes(3), Duration.ofMinutes(3));
        String taskJson = gson.toJson(updatedTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<Task> tasks = taskManager.getAllTasks();
        System.out.println(tasks);

        Assertions.assertNotNull(tasks, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals("Spring boot", tasks.get(0).getName());
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 12, 0);

        Task existingTask = new Task(1, "Spring Boot", "Изучаем Spring Boot", Status.NEW, time, Duration.ofMinutes(10));
        String taskJson = gson.toJson(existingTask);

        HttpClient client = HttpClient.newHttpClient();
        URI createUrl = URI.create("http://localhost:8080/api/tasks");
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(createUrl)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, createResponse.statusCode(), "Ошибка при создании задачи");

        URI getUrl = URI.create("http://localhost:8080/api/tasks/1");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, getResponse.statusCode(), "Ошибка при получении задачи");

        Task returnedTask = gson.fromJson(getResponse.body(), Task.class);

        Assertions.assertNotNull(returnedTask, "Задача не возвращается");
        Assertions.assertEquals(existingTask.getId(), returnedTask.getId(), "ID задачи не совпадает");
        Assertions.assertEquals(existingTask.getName(), returnedTask.getName(), "Имя задачи не совпадает");
        Assertions.assertEquals(existingTask.getDescription(), returnedTask.getDescription(), "Описание задачи не совпадает");
        Assertions.assertEquals(existingTask.getStatus(), returnedTask.getStatus(), "Статус задачи не совпадает");
        Assertions.assertEquals(existingTask.getStartTime(), returnedTask.getStartTime(), "Время начала задачи не совпадает");
        Assertions.assertEquals(existingTask.getDuration(), returnedTask.getDuration(), "Длительность задачи не совпадает");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task(1, "Spring Boot", "Учим Bean", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI createUrl = URI.create("http://localhost:8080/api/tasks");
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(createUrl)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, createResponse.statusCode(), "Ошибка при создании задачи");

        URI deleteUrl = URI.create("http://localhost:8080/api/tasks/1");
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(deleteUrl)
                .DELETE().build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, deleteResponse.statusCode(), "Ошибка при удалении задачи");

        URI getUrl = URI.create("http://localhost:8080/api/tasks");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = gson.fromJson(getResponse.body(), new TaskTypeToken().getType());

        Assertions.assertNotNull(tasks, "Список задач должен быть не null");
        Assertions.assertTrue(tasks.isEmpty(), "Задача не была удалена");
    }

    @Test
    public void testCreateTaskWithEmptyBody() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode(), "Сервер должен вернуть Bad Request для пустого тела запроса");
    }

    @Test
    public void testGetTaskWithInvalidId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks/999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode(), "Сервер должен вернуть Not Found для несуществующего ID");
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(10));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Сервер должен вернуть статус 200 OK");

        List<Task> tasks = gson.fromJson(response.body(), new TaskTypeToken().getType());

        Assertions.assertNotNull(tasks, "Список задач не должен быть null");
        Assertions.assertEquals(2, tasks.size(), "Количество задач не совпадает");
        Assertions.assertEquals("Task 1", tasks.get(0).getName(), "Имя первой задачи не совпадает");
        Assertions.assertEquals("Task 2", tasks.get(1).getName(), "Имя второй задачи не совпадает");
    }

    @Test
    public void testGetAllTasksWhenEmpty() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Сервер должен вернуть статус 200 OK");
        List<Task> tasks = gson.fromJson(response.body(), new TaskTypeToken().getType());
        Assertions.assertNotNull(tasks, "Список задач не должен быть null");
        Assertions.assertTrue(tasks.isEmpty(), "Список задач должен быть пуст");
    }
}
