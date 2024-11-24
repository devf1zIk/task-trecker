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
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = Files.createTempFile("tasks", ".csv").toFile();
        tempFile.deleteOnExit();
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    public void testAddTask() {
        Task task = new Task("Task 1", Status.NEW, "Test Task");
        manager.addTask(task);

        assertEquals(1, manager.getAllTasks().size());
        assertTrue(manager.getAllTasks().contains(task));
    }

    @Test
    public void testAddEpic() {
        Epic epic = new Epic(1, "Epic 1", "Test Epic");
        manager.addEpic(epic);

        assertEquals(1, manager.getAllEpics().size());
        assertTrue(manager.getAllEpics().contains(epic));
    }

    @Test
    public void testAddSubtask() {
        Epic epic = new Epic(1, "Epic 1", "Test Epic");
        manager.addEpic(epic);
        SubTask subtask = new SubTask(1,"Subtask 1", "Test Subtask", Status.NEW, epic.getId());
        manager.addSubtask(subtask);

        assertEquals(1, manager.getAllSubtasks().size());
        assertTrue(manager.getAllSubtasks().contains(subtask));
    }

    @Test
    public void testRemoveTask() {
        Task task = new Task("Task 1", Status.NEW, "Test Task");
        manager.addTask(task);
        manager.removeTask(task.getId());
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Task 1", Status.NEW, "Test Task");
        manager.addTask(task);
        task.setName("Updated Task");
        manager.updateTask(task);

        assertEquals("Updated Task", manager.getAllTasks().get(0).getName());
    }

    @Test
    public void testSaveAndLoadTasks() throws IOException {
        Task task = new Task("Task 1", Status.NEW, "Description 1");
        Epic epic = new Epic(1, "Epic 1", "Epic Description");
        SubTask subtask = new SubTask(1, "Subtask 1", "Subtask Description", Status.NEW, epic.getId());

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size());
        Task loadedTask = loadedManager.getAllTasks().get(0);
        assertEquals(task.getId(), loadedTask.getId());
        assertEquals(task.getName(), loadedTask.getName());
        assertEquals(task.getStatus(), loadedTask.getStatus());
        assertEquals(task.getDescription(), loadedTask.getDescription());

        assertEquals(1, loadedManager.getAllEpics().size());
        Epic loadedEpic = loadedManager.getAllEpics().get(0);
        assertEquals(epic.getId(), loadedEpic.getId());
        assertEquals(epic.getName(), loadedEpic.getName());
        assertEquals(epic.getStatus(), loadedEpic.getStatus());
        assertEquals(epic.getDescription(), loadedEpic.getDescription());
    }


    @Test
    public void testLoadFromFileWithCorruptedData() throws IOException {
        String fileContent = """
                id,type,name,status,description,epic
                1,TASK,Task 1,NEW,Description 1,
                invalid,data,line
                """;
        Files.writeString(tempFile.toPath(), fileContent);

        Exception exception = assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(tempFile);
        });

        assertTrue(exception.getMessage().contains("Некорректные данные в строке"),
                "Сообщение об ошибке должно указывать на некорректные данные.");
    }

    @Test
    public void testLoadFromCorruptedFile() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        try {
            Files.writeString(tempFile.toPath(), "Некорректные данные");
        } catch (IOException e) {
            fail("Ошибка при записи в файл.");
        }
        assertDoesNotThrow(() -> {
            FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);
            assertTrue(loadedManager.getAllTasks().isEmpty(), "Менеджер должен быть пустым при некорректных данных.");
        });
    }
}
