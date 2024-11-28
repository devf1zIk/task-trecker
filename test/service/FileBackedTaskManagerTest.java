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
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static service.file.FileBackedTaskManager.loadFromFile;

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
        FileBackedTaskManager emptyManager = loadFromFile(TEST_FILE);

        Assertions.assertTrue(emptyManager.getAllTasks().isEmpty());
        Assertions.assertTrue(emptyManager.getAllEpics().isEmpty());
        Assertions.assertTrue(emptyManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldRemoveTask() {
        Task task = new Task(3,"Task","description",Status.NEW,startTime,duration,endTime);
        manager.addTask(task);
        manager.removeTask(task.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(TEST_FILE);
        assertFalse(loadedManager.getAllTasks().contains(task));
    }

    @Test
    void shouldRemoveEpic() {
        Epic epic = new Epic(1, "Epic 1", "Description", Status.NEW, startTime, duration);
        manager.addEpic(epic);
        manager.removeEpic(epic.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(TEST_FILE);
        assertFalse(loadedManager.getAllEpics().contains(epic));
    }

    @Test
    void shouldCalculateEpicStatusCorrectly_allSubtasksNew() {
        Epic epic = new Epic(1, "Epic 1", "Description", Status.NEW, startTime, duration);
        manager.addEpic(epic);

        SubTask subtask1 = new SubTask(1, "Subtask 1", "Description", Status.NEW, startTime,duration, epic.getId());
        manager.addSubtask(subtask1);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void shouldCalculateEpicStatusCorrectly_allSubtasksDone() {
        Epic epic = new Epic(1, "Epic 1", "Description", Status.NEW, startTime, duration);
        manager.addEpic(epic);

        SubTask subtask1 = new SubTask(1, "Subtask 1", "Description", Status.DONE, startTime, duration, epic.getId());
        manager.addSubtask(subtask1);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void shouldCalculateEpicStatusCorrectly_mixedSubtasks() {
        Epic epic = new Epic(1, "Epic 1", "Description", Status.NEW, startTime, duration);
        manager.addEpic(epic);

        SubTask subtask1 = new SubTask(1, "Subtask 1", "Description", Status.NEW, startTime, duration, epic.getId());
        manager.addSubtask(subtask1);

        assertEquals(Status.NEW, epic.getStatus());
    }



}


