package service;
import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.file.FileBackedTaskManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private Path path = null;


    @BeforeEach
    public void init() {
        try {
            path = Files.createTempFile("tasks", ".csv");
        } catch (IOException ignored) {
        }
    }

    @AfterEach
    public void delete() {
        if (path != null) {
            path.toFile().deleteOnExit();
        }
    }

    @Test
    public void shouldSaveWithoutTasks() {
        assertEquals(path.toFile().length(), 0, "Файл не пустой");

        FileBackedTaskManager manager = new FileBackedTaskManager(path.toFile());
        int lines = getCountFileLines(path);

        assertEquals(lines, 1, "Количество строк в файле не равно 1");
    }

    @Test
    public void shouldSaveTasks() {
        assertEquals(path.toFile().length(), 0, "Файл не пустой");

        FileBackedTaskManager manager = new FileBackedTaskManager(path.toFile());
        manager.addTask(new Task("Task add","task dsc",Status.NEW));
        manager.addEpic(new Epic(1,"epic","epic test",Status.NEW));
        manager.addSubtask(new SubTask("subtask","subtask",Status.NEW));
        int lines = getCountFileLines(path);

        assertEquals(lines, 4, "Количество строк в файле не равно 4");
    }

    private int getCountFileLines(Path path) {
        int lines = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            while (br.readLine() != null) {
                lines++;
            }
        } catch (IOException ignored) {
        }

        return lines;
    }

    @Test
    public void shouldLoadFromEmptyFile() {
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(path.toFile());

        assertEquals(manager.getAllTasks().size(), 0, "Колиичество задач больше 0");
        assertEquals(manager.getAllEpics().size(), 0, "Колиичество эпиков больше 0");
        assertEquals(manager.getAllSubtasks().size(), 0, "Колиичество подзадач больше 0");
    }

    @Test
    public void shouldLoadFromFile() {
        FileBackedTaskManager manager = new FileBackedTaskManager(path.toFile());
        manager.addTask(new Task("Task 1", "Task 1", Status.NEW));
        manager.addEpic(new Epic(1,"epic1","description epic",Status.NEW));
        manager.addSubtask(new SubTask("SubTask 1", "SubTask description", Status.NEW));

        assertNotEquals(path.toFile().length(), 0, "Файл пустой");

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(path.toFile());

        assertNotEquals(newManager.getAllTasks().size(), 0, "Колиичество задач 0");
        assertNotEquals(newManager.getAllEpics().size(), 0, "Колиичество эпиков 0");
        assertNotEquals(newManager.getAllSubtasks().size(), 0, "Колиичество подзадач 0");
    }
}
