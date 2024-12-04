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
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

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
        Task task = new Task(7,"taskname","descr",Status.NEW,LocalDateTime.of(2013,4,5,6,7,8),Duration.ofMinutes(4));
        System.out.println(task);
        taskManager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loadedManager.getTask(task.getId());

        assertNotNull(loadedTask, "Задача должна быть восстановлена из файла.");
        assertEquals(task.getId(), loadedTask.getId(), "ID задачи не совпадает.");
        assertEquals(task.getName(), loadedTask.getName(), "Название задачи не совпадает.");
        assertEquals(task.getDescription(), loadedTask.getDescription(), "Описание задачи не совпадает.");
        assertEquals(task.getStatus(), loadedTask.getStatus(), "Статус задачи не совпадает.");
        assertEquals(task.getStartTime(), loadedTask.getStartTime(), "Время начала задачи не совпадает.");
        assertEquals(task.getDuration(), loadedTask.getDuration(), "Продолжительность задачи не совпадает.");
    }



    @Test
    void shouldCalculateEndTimeBasedOnLatestSubtask() {

        Epic epic1 = new Epic(1, "Epic 1", "Description 1", Status.NEW, LocalDateTime.of(2015, 4, 5, 6, 7, 8), Duration.ofMinutes(3));
        taskManager.addEpic(epic1);

        System.out.println(epic1);
        System.out.println(epic1.getId());
        SubTask subTask1 = new SubTask(2, "Subtask 1", "Description 1", Status.NEW, LocalDateTime.of(2024, 12, 1, 10, 0), Duration.ofMinutes(30), epic1.getId());
        SubTask subTask2 = new SubTask(3, "Subtask 2", "Description 2", Status.NEW, LocalDateTime.of(2024, 12, 1, 11, 0), Duration.ofMinutes(45), epic1.getId());
        System.out.println(epic1.getId());
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);


        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        Epic loadedEpic = loadedManager.getEpic(epic1.getId());
        assertNotNull(loadedEpic, "Эпик не был восстановлен!");

        assertEquals(2, loadedEpic.getSubTasks().size(), "Количество подзадач не совпадает.");
        assertTrue(loadedEpic.getSubTasks().contains(subTask1.getId()), "Подзадача 1 не восстановлена.");
        assertTrue(loadedEpic.getSubTasks().contains(subTask2.getId()), "Подзадача 2 не восстановлена.");

        loadedManager.updateEpicTimeAndDuration(loadedEpic);

        assertEquals(LocalDateTime.of(2024, 12, 1, 11, 45), loadedEpic.getEndTime(), "Время завершения эпика должно быть основано на самой поздней подзадаче.");
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
    void shouldDeleteTaskAndRemoveFromFile() {
        Task task = new Task(1, "Task 1", "Description 1", Status.NEW, LocalDateTime.of(2000, 12, 3, 4, 5, 6), Duration.ofMinutes(2));
        taskManager.addTask(task);

        taskManager.removeTask(task.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertNull(loadedManager.getTask(task.getId()), "Удалённая задача не должна быть доступна из файла.");
    }

}


