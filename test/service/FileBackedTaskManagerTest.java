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
    public void testSaveAndLoadEmptyFile() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Список задач должен быть пустым.");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Список эпиков должен быть пустым.");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пустым.");
    }

    @Test
    public void testLoadFromFileWithTasks() throws IOException {
        String fileContent = """
                id,type,name,status,description,epic
                1,TASK,Task 1,NEW,Description 1,
                2,EPIC,Epic 1,NEW,Description Epic,
                3,SUBTASK,Subtask 1,IN_PROGRESS,Description Subtask,2
                """;
        Files.writeString(tempFile.toPath(), fileContent);

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, manager.getAllTasks().size(), "Неверное количество задач.");
        assertEquals(1, manager.getAllEpics().size(), "Неверное количество эпиков.");
        assertEquals(1, manager.getAllSubtasks().size(), "Неверное количество подзадач.");

        Task task = manager.getAllTasks().get(0);
        assertEquals("Task 1", task.getName(), "Имя задачи некорректно.");
        assertEquals(Status.NEW, task.getStatus(), "Статус задачи некорректен.");

        Epic epic = manager.getAllEpics().get(0);
        assertEquals("Epic 1", epic.getName(), "Имя эпика некорректно.");
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика некорректен.");

        SubTask subTask = manager.getAllSubtasks().get(0);
        assertEquals("Subtask 1", subTask.getName(), "Имя подзадачи некорректно.");
        assertEquals(Status.IN_PROGRESS, subTask.getStatus(), "Статус подзадачи некорректен.");
        assertEquals(epic.getId(), subTask.getEpicId(), "ID эпика для подзадачи некорректен.");
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
