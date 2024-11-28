package service;
import exception.ValidationException;
import model.Task;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.managers.InMemoryTaskManager;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;
    private final LocalDateTime startTime = LocalDateTime.now();
    private final Duration duration = Duration.ofMinutes(1);
    private InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void shouldAddAndRetrieveTask() {
        Task task = new Task(1, "Task 1", "Description", Status.NEW, startTime, duration);
        manager.addTask(task);

        Task retrievedTask = manager.getTask(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task.getId(), retrievedTask.getId());
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task(2, "Task 1", "Description", Status.NEW, startTime, duration);
        manager.addTask(task);

        task.setName("Updated Task 1");
        manager.updateTask(task);

        Task updatedTask = manager.getTask(task.getId());
        assertNotNull(updatedTask);
        assertEquals("Updated Task 1", updatedTask.getName());
    }

    @Test
    void shouldDeleteTask() {
        Task task = new Task(3, "Task 1", "Description", Status.NEW, startTime, duration);
        manager.addTask(task);

        manager.removeTask(task.getId());
        assertNull(manager.getTask(task.getId()));
    }

    @Test
    void shouldRetrieveAllTasks() {

        Task task1 = new Task(4, "Task 1", "Description", Status.NEW, startTime, duration);
        Task task2 = new Task(5, "Task 2", "Description", Status.IN_PROGRESS, startTime.plusMinutes(2), duration);
        manager.addTask(task1);
        manager.addTask(task2);

        List<Task> tasks = manager.getAllTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    void shouldThrowValidationExceptionOnOverlappingTasks() {
        Task task1 = new Task(6, "Task 1", "Description", Status.NEW, startTime, duration);
        Task task2 = new Task(7, "Task 2", "Description", Status.NEW, startTime, duration);
        manager.addTask(task1);

        ValidationException exception = assertThrows(ValidationException.class, () -> manager.addTask(task2));
        assertEquals("Task overlaps with an existing task.", exception.getMessage());
    }

    @Test
    void shouldRetrieveHistory() {
        Task task1 = new Task(1, "Task 1", "Description", Status.NEW, startTime, duration);
        Task task2 = new Task(2, "Task 2", "Description", Status.IN_PROGRESS, startTime.plusMinutes(2), duration);

        manager.addTask(task1);
        manager.addTask(task2);

        manager.getTask(task1.getId());
        manager.getTask(task2.getId());

        List<Task> history = manager.getHistory();

        assertEquals(2, history.size());
        assertTrue(history.contains(task1));
        assertTrue(history.contains(task2));
    }
}



