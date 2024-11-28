package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.file.FileBackedTaskManager;
import java.io.File;
import java.time.Duration;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {

    private static final File TEST_FILE = new File("tasks.csv");
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new FileBackedTaskManager(TEST_FILE);
    }

    @Test
    void shouldHandleEmptyFile() {

        FileBackedTaskManager emptyManager = FileBackedTaskManager.loadFromFile(TEST_FILE);

        assertTrue(emptyManager.getAllTasks().isEmpty());
        assertTrue(emptyManager.getAllEpics().isEmpty());
        assertTrue(emptyManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteTasksAndSaveChanges() {

        Task task1 = new Task(1, "Design Database", "Create ERD for the project", Status.NEW, Duration.ofHours(3));
        Task task2 = new Task(2, "Setup Server", "Install necessary software on the server", Status.IN_PROGRESS, Duration.ofHours(5));
        Task task3 = new Task(3, "Code Review", "Review the latest merge request", Status.DONE, Duration.ofHours(2));
        Task task4 = new Task(4, "Write Tests", "Create unit tests for TaskManager", Status.NEW, Duration.ofHours(4));
        Task task5 = new Task(5, "Fix Bugs", "Resolve reported issues in the bug tracker", Status.IN_PROGRESS, Duration.ofHours(6));

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(task4);
        manager.addTask(task5);
        manager.deleteAllTasks();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(TEST_FILE);

        assertTrue(loadedManager.getAllTasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadEpicsWithSubtasksCorrectly() {

        Epic epic1 = new Epic(6, "Backend Development", "Implement backend functionality", Status.NEW, Duration.ofDays(3));
        Epic epic2 = new Epic(7, "Frontend Development", "Build the user interface", Status.IN_PROGRESS, Duration.ofDays(4));

        manager.addEpic(epic1);
        manager.addEpic(epic2);

        SubTask subtask = new SubTask(1,"Subtask", "Subtask Description", Status.NEW, Duration.ofHours(1), epic1.getId());
        SubTask subTask2 = new SubTask(2,"Subtask","subtask description", Status.NEW, Duration.ofHours(2), epic2.getId());

        manager.addSubtask(subtask);
        manager.addSubtask(subTask2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(TEST_FILE);

        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());
        assertTrue(loadedManager.getAllEpics().get(0).getSubTasks().contains(subtask.getId()));
    }

}

