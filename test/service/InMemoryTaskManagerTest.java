package service;

import model.Epic;
import model.SubTask;
import model.Task;
import java.time.Duration;
import java.util.List;
import model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.managers.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void addTask_shouldAddTaskToManager() {
        Task task = new Task("Task 1", "Description", Status.NEW,Duration.ofMinutes(2));
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTask(task.getId());
        assertNotNull(retrievedTask, "Task should be added.");
        assertEquals(task.getName(), retrievedTask.getName(), "Task name should match.");
    }

    @Test
    void addEpic_shouldAddEpicToManager() {
        Epic epic = new Epic(1, "Epic 1", "Description", Status.NEW,Duration.ofHours(2));
        taskManager.addEpic(epic);

        Epic retrievedEpic = taskManager.getEpic(epic.getId());
        assertNotNull(retrievedEpic, "Epic should be added.");
        assertEquals(epic.getName(), retrievedEpic.getName(), "Epic name should match.");
    }

    @Test
    void addSubtask_shouldAddSubtaskToEpic() {
        Epic epic = new Epic(2,"Epic 1", "Description", Status.NEW,Duration.ofMinutes(2));
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask(1,"Subtask 1", "Description", Status.NEW,Duration.ofHours(2),epic.getId());
        taskManager.addSubtask(subTask);
        List<SubTask> subtasks = taskManager.getSubtasksOfEpic(epic.getId());
        assertEquals(1, subtasks.size(), "Epic should contain one subtask.");
        assertEquals(subTask.getId(), subtasks.get(0).getId(), "Subtask ID should match.");
    }

    @Test
    void updateTask_shouldUpdateExistingTask() {
        Task task = new Task("Task 1", "Description", Status.NEW,Duration.ofHours(1));
        taskManager.addTask(task);

        task.setName("Updated Task");
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTask(task.getId());
        assertEquals("Updated Task", updatedTask.getName(), "Task name should be updated.");
    }

    @Test
    void removeTask_shouldRemoveTaskFromManager() {
        Task task = new Task("Task 1", "Description", Status.NEW, Duration.ofHours(1));
        taskManager.addTask(task);

        taskManager.removeTask(task.getId());

        assertNull(taskManager.getTask(task.getId()), "Task should be removed.");
    }

}

