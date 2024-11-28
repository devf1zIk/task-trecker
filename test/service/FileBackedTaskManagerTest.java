package service;

import exception.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.file.FileBackedTaskManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
    void shouldSaveAndLoadTasksCorrectly() {
        Task task = new Task("taskname","description",Status.NEW,Duration.ofMinutes(10));
        manager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(TEST_FILE);

        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(task, loadedManager.getAllTasks().get(0));
    }

    @Test
    void shouldHandleEmptyFile() {

        FileBackedTaskManager emptyManager = FileBackedTaskManager.loadFromFile(TEST_FILE);

        assertTrue(emptyManager.getAllTasks().isEmpty());
        assertTrue(emptyManager.getAllEpics().isEmpty());
        assertTrue(emptyManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldThrowExceptionOnInvalidFileFormat() {

        try (Writer writer = new FileWriter(TEST_FILE)) {
            writer.write("Invalid Data\n");
        } catch (IOException e) {
            fail("Setup failed: unable to write to test file");
        }

        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(TEST_FILE));
    }

    @Test
    void shouldDeleteTasksAndSaveChanges() {

        Task task = new Task("Test Task", "Description", Status.NEW, Duration.ofHours(1));
        manager.addTask(task);
        manager.deleteAllTasks();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(TEST_FILE);

        assertTrue(loadedManager.getAllTasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadEpicsWithSubtasksCorrectly() {

        Epic epic = new Epic(1,"Epic", "Epic Description", Status.NEW,Duration.ofHours(1));
        manager.addEpic(epic);
        SubTask subtask = new SubTask(1,"Subtask", "Subtask Description", Status.NEW, Duration.ofHours(1), epic.getId());
        manager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(TEST_FILE);

        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());
        assertTrue(loadedManager.getAllEpics().get(0).getSubTasks().contains(subtask.getId()));
    }

}

