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
    protected Duration duration = Duration.ofSeconds(10);

    @BeforeEach
    void setUp() throws IOException {
        task1 = new Task(1,"task1","description",Status.NEW,startTime,duration);
        task2 = new Task(2,"task2","description",Status.NEW,startTime,duration);
        task3 = new Task(3,"task3","description",Status.NEW,startTime,duration);

        epic = new Epic(1, "Epic 1", "Description 1", Status.NEW,startTime, duration);
        epic2 = new Epic(2, "Epic 2", "Description 2", Status.NEW,startTime, duration);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);

        subTask1 = new SubTask(1, "Subtask 1", "Description 1", Status.NEW,startTime,duration, epic.getId());
        subTask2 = new SubTask(2, "Subtask 2", "Description 2", Status.NEW,startTime,duration, epic2.getId());

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

