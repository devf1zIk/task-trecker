package service;

import exception.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.file.FileBackedTaskManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private static final String Stroke = "id,type,name,description,status,startTime,duration,endTime,epicId\n";
    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(tempFile);
    }

    @BeforeEach
    void setUp() {
        tempFile = new File("temporary.csv");
        FileBackedTaskManager manager = createTaskManager();
        super.setUp();
    }

    @Test
    void shouldSaveAndLoadTaskFromFile() {
        Task task = new Task(1, "Task 1", "Description 1", Status.NEW, LocalDateTime.of(2000, 12, 3, 4, 5, 6), Duration.ofMinutes(2));
        taskManager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loadedManager.getTask(task.getId());

        assertEquals(task, loadedTask, "Задача, добавленная в файл, должна быть восстановлена из файла.");
    }

    @Test
    void shouldSaveAndLoadEpicFromFile() {
        Epic epic = new Epic(1, "Epic 1", "Description 1", Status.NEW, Duration.ofMinutes(1));
        taskManager.addEpic(epic);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Epic loadedEpic = loadedManager.getEpic(epic.getId());

        assertEquals(epic, loadedEpic, "Эпик, добавленный в файл, должен быть восстановлен из файла.");
    }

    @Test
    void shouldSaveAndLoadSubtaskFromFile() {
        Epic epic = new Epic(4, "Epic 1", "Description 1", Status.NEW, Duration.ofMinutes(1));
        taskManager.addEpic(epic);

        SubTask subtask = new SubTask(5, "SubTask 1", "Description 1", Status.NEW, LocalDateTime.of(2016, 4, 5, 6, 7, 8), Duration.ofMinutes(1), epic.getId());
        taskManager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        SubTask loadedSubtask = loadedManager.getSubtask(subtask.getId());

        assertEquals(subtask, loadedSubtask, "Подзадача, добавленная в файл, должна быть восстановлена из файла.");
    }

    @Test
    void shouldNotLoadTaskWithInvalidData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("id,type,name,description,status,startTime,duration,endTime,epicId\n");
            writer.write("1,INVALID_TYPE,Task 1,Description 1,NEW,2000-12-03T04:05:06,PT2M,,\n");
        } catch (IOException e) {
            fail("Не удалось записать тестовые данные в файл.");
        }

        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(tempFile);
        }, "Менеджер должен выбросить исключение при попытке загрузить некорректные данные.");
    }

    @Test
    void shouldUpdateTaskAndSaveToFile() {
        Task task = new Task(1, "Task 1", "Description 1", Status.NEW, LocalDateTime.of(2000, 12, 3, 4, 5, 6), Duration.ofMinutes(2));
        taskManager.addTask(task);

        task.setName("Updated Task");
        taskManager.updateTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task updatedTask = loadedManager.getTask(task.getId());

        assertEquals("Updated Task", updatedTask.getName(), "Обновлённая задача должна быть сохранена в файл.");
    }

    @Test
    void shouldDeleteTaskAndRemoveFromFile() {
        Task task = new Task(1, "Task 1", "Description 1", Status.NEW, LocalDateTime.of(2000, 12, 3, 4, 5, 6), Duration.ofMinutes(2));
        taskManager.addTask(task);

        taskManager.removeTask(task.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertNull(loadedManager.getTask(task.getId()), "Удалённая задача не должна быть доступна из файла.");
    }

    @Test
    void shouldHandleFileWriteException() {
        File invalidFile = new File("temporary.csv");
        FileBackedTaskManager managerWithInvalidFile = new FileBackedTaskManager(invalidFile);

        assertThrows(ManagerSaveException.class, () -> {
        }, "Менеджер должен выбросить исключение при попытке записи в файл с недоступным путём.");
    }

}


