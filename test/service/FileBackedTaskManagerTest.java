package service;
import exception.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.file.FileBackedTaskManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void shouldSaveAndLoadTasksCorrectly() {
        Task task = new Task(1, "Task 1", "Description 1", Status.NEW);
        manager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(task, loadedManager.getTask(1));
    }

    @Test
    void shouldSaveAndLoadEpicsAndSubtasksCorrectly() {
        Epic epic = new Epic(1, "Epic 1", "Epic Description");
        SubTask subTask = new SubTask(2,"SubTask 1", "SubTask Description", Status.NEW,epic.getId());
        manager.addEpic(epic);
        manager.addSubtask(subTask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(epic, loadedManager.getEpic(1));

        assertEquals(1, loadedManager.getAllSubtasks().size());
        assertEquals(subTask, loadedManager.getSubtask(2));

        assertTrue(loadedManager.getEpic(1).getSubTasks().contains(2));
    }

    @Test
    void shouldHandleEmptyFileCorrectly() throws IOException {
        File tempFile = File.createTempFile("test", ".txt");
        tempFile.deleteOnExit();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }


    @Test
    void shouldThrowExceptionForCorruptedFile() throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();

        Files.writeString(tempFile.toPath(), "id,type,name,status,description\n1,Task,Task 1,NEW,");

        ManagerSaveException exception = assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(tempFile);
        });

        assertTrue(exception.getMessage().contains("Ошибка при разборе строки"));
    }


    @Test
    void shouldSaveAndLoadMultipleEntities() {
        Task task = new Task(1, "Task 1", "Description 1", Status.NEW);
        Epic epic = new Epic(2, "Epic 1", "Epic Description");
        SubTask subTask = new SubTask(1, "SubTask 1", "SubTask Description", Status.DONE, epic.getId());

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subTask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(task, loadedManager.getTask(1));
        assertEquals(task.getId(), loadedManager.getTask(1).getId());
        assertEquals(task.getName(), loadedManager.getTask(1).getName());
        assertEquals(task.getDescription(), loadedManager.getTask(1).getDescription());
        assertEquals(task.getStatus(), loadedManager.getTask(1).getStatus());

        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(epic, loadedManager.getEpic(2));
        assertEquals(epic.getId(), loadedManager.getEpic(2).getId());
        assertEquals(epic.getName(), loadedManager.getEpic(2).getName());
        assertEquals(epic.getDescription(), loadedManager.getEpic(2).getDescription());

        assertEquals(1, loadedManager.getAllSubtasks().size());
        assertEquals(subTask, loadedManager.getSubtask(3));
        assertEquals(subTask.getId(), loadedManager.getSubtask(3).getId());
        assertEquals(subTask.getName(), loadedManager.getSubtask(3).getName());
        assertEquals(subTask.getDescription(), loadedManager.getSubtask(3).getDescription());
        assertEquals(subTask.getStatus(), loadedManager.getSubtask(3).getStatus());
        assertEquals(subTask.getEpicId(), loadedManager.getSubtask(3).getEpicId());

    }
}

