package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.managers.TaskManager;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task1;
    protected Task task2;
    protected Task task3;
    protected Epic epic;
    protected Epic epic2;
    protected SubTask subTask1;
    protected SubTask subTask2;
    protected LocalDateTime startTime = LocalDateTime.now();
    protected Duration duration = Duration.ofMinutes(1);

    @BeforeEach
    void setUp() throws IOException {
        task1 = new Task(50,"spring boot1","java ee",Status.NEW,LocalDateTime.of(1,1,1,1,1,1),Duration.ofMinutes(1));
        task2 = new Task(51,"spring boot2","java se",Status.NEW,LocalDateTime.of(2,10,11,12,33,45),Duration.ofMinutes(3));
        task3 = new Task(52,"spring boot3","fastapi",Status.NEW,LocalDateTime.of(4,4,4,4,4,4),Duration.ofMinutes(4));

        epic = new Epic(55, "postgresql 1", "postgresql 1", Status.NEW,LocalDateTime.of(5,5,5,5,5,5),Duration.ofMinutes(3));
        epic2 = new Epic(56, "postgresql 2", "mysql 2", Status.NEW,LocalDateTime.of(11,10,17,17,17,17),Duration.ofMinutes(3));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);

        subTask1 = new SubTask(6, "task is done", "task", Status.NEW,LocalDateTime.of(12,5,4,2,5,6),Duration.ofMinutes(10), epic.getId());
        subTask2 = new SubTask(7, "tasks", "descr", Status.NEW,LocalDateTime.of(16,2,4,6,7,11),Duration.ofMinutes(1), epic2.getId());

        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);
    }

    @Test
    void shouldSetEpicStatusToNewWhenAllSubtasksAreNew() {
        taskManager.updateStatus(epic);
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void shouldSetEpicStatusToInProgressWhenAnySubtaskIsInProgress() {
        subTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subTask1);

        taskManager.updateStatus(epic);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldSetEpicStatusToDoneWhenAllSubtasksAreDone() {
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subTask1);
        taskManager.updateSubtask(subTask2);

        taskManager.updateStatus(epic);
        Assertions.assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void shouldSetEpicStatusToNewWhenSubtasksAreNotDoneAndNotInProgress() {
        subTask1.setStatus(Status.NEW);
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subTask1);
        taskManager.updateSubtask(subTask2);

        taskManager.updateStatus(epic);
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void shouldSetEpicStatusToNewWhenNoSubtasksExist() {
        taskManager.removeSubtask(subTask1.getId());
        taskManager.removeSubtask(subTask2.getId());

        taskManager.updateStatus(epic);
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }
}

