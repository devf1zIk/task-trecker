package service;
import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.file.FileBackedTaskManager;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {

    private static final File TEST_FILE = new File("tasks.csv");
    private FileBackedTaskManager manager;
    private final LocalDateTime startTime = LocalDateTime.now();
    private final Duration duration = Duration.ofMinutes(1);
    private final LocalDateTime endTime = startTime.plus(duration);

    @BeforeEach
    void setUp() {
        manager = new FileBackedTaskManager(TEST_FILE);
    }

    @Test
    void shouldHandleEmptyFile() {
        FileBackedTaskManager emptyManager = FileBackedTaskManager.loadFromFile(TEST_FILE);

        Assertions.assertTrue(emptyManager.getAllTasks().isEmpty());
        Assertions.assertTrue(emptyManager.getAllEpics().isEmpty());
        Assertions.assertTrue(emptyManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteTasksAndSaveChanges() {
        Task task10 = new Task(9, "Spring Boot 9", "Spring Boot is beautiful", Status.NEW,startTime, duration,endTime);
        Task task11 = new Task(10, "Spring Boot 5", "Spring Boot is java", Status.NEW,startTime , duration, endTime);
        Task task12 = new Task(11, "Spring Boot 7", "Spring boot is jvm", Status.NEW, startTime, duration, endTime);

        manager.addTask(task10);
        manager.addTask(task11);
        manager.addTask(task12);

        manager.removeTask(task10.getId());
        manager.removeTask(task11.getId());

        task12.setName("Spring Boot 99");
        task12.setDescription("Spring boot old language");
        task12.setStatus(Status.NEW);
        task12.setStartTime(startTime);
        task12.setDuration(duration);
        task12.getEndTime();

        manager.updateTask(task12);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(TEST_FILE);

        assertFalse("Task with ID 8 should be removed.", loadedManager.getAllTasks().stream().anyMatch(task -> task.getId() == 8));

        Task loadedTask9 = loadedManager.getTask(9);
        assertEquals("JavaScript", loadedTask9.getName(), "Task 9 title should be updated.");
        assertEquals("JS is better", loadedTask9.getDescription(), "Task 9 description should be updated.");

        assertEquals(1, loadedManager.getAllTasks().size(), "Only one task should remain.");
    }


    @Test
    void shouldSaveAndLoadEpicsWithSubtasksCorrectly() {
        Epic epic4 = new Epic(4, "epic4", "Epic is bomb", Status.NEW, duration);
        Epic epic5 = new Epic(5, "epic5", "Epic is 5", Status.NEW, duration);

        manager.addEpic(epic4);
        manager.addEpic(epic5);

        manager.updateEpic(epic4);
        manager.removeEpic(epic5.getId());

        SubTask subtask = new SubTask(3, "Java subtask", "I have finished this task", Status.NEW, Duration.ofMinutes(1), epic4.getId());
        SubTask subTask2 = new SubTask(4, "Spring", "I have spring task", Status.NEW, Duration.ofMinutes(2), epic5.getId());

        manager.addSubtask(subtask);
        manager.addSubtask(subTask2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(TEST_FILE);

        assertEquals(1, loadedManager.getAllEpics().size(), "There should be only one epic.");
        assertEquals(2, loadedManager.getAllSubtasks().size(), "There should be two subtasks.");
        Assertions.assertTrue(loadedManager.getAllEpics().get(0).getSubTasks().contains(subtask.getId()), "Epic should contain the subtask.");
    }
}


