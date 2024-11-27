package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import service.managers.InMemoryTaskManager;
import java.util.List;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager manager;
    private Task task1;
    private Task task2;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void testAddTask() {
        manager.addTask(task1);
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals(task1, tasks.get(0));
    }

    @Test
    void testAddEpic() {
        manager.addEpic(epic);
        List<Epic> epics = manager.getAllEpics();
        assertEquals(1, epics.size());
        assertEquals(epic, epics.get(0));
    }

    @Test
    void testAddSubTask() {
        manager.addEpic(epic);
        subTask.setId(epic.getId());
        manager.addSubtask(subTask);

        List<SubTask> subtasks = manager.getAllSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals(subTask, subtasks.get(0));
    }

    @Test
    void testUpdateTask() {
        manager.addTask(task1);
        task1.setName("Updated Task 1");
        manager.updateTask(task1);

        Task updatedTask = manager.getTask(task1.getId());
        assertEquals("Updated Task 1", updatedTask.getName());
    }

    @Test
    void testUpdateEpic() {
        manager.addEpic(epic);
        epic.setName("Updated Epic 1");
        manager.updateEpic(epic);

        Epic updatedEpic = manager.getEpic(epic.getId());
        assertEquals("Updated Epic 1", updatedEpic.getName());
    }

    @Test
    void testUpdateSubTask() {
        manager.addEpic(epic);
        subTask.setId(epic.getId());
        manager.addSubtask(subTask);
        subTask.setName("Updated SubTask 1");
        manager.updateSubtask(subTask);

        SubTask updatedSubTask = manager.getSubtask(subTask.getId());
        assertEquals("Updated SubTask 1", updatedSubTask.getName());
    }

    @Test
    void testRemoveTask() {
        manager.addTask(task1);
        manager.removeTask(task1.getId());

        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void testRemoveEpic() {
        manager.addEpic(epic);
        manager.removeEpic(epic.getId());

        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    void testRemoveSubTask() {
        manager.addEpic(epic);
        subTask.setId(epic.getId());
        manager.addSubtask(subTask);
        manager.removeSubtask(subTask.getId());

        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    void testHasOverlaps() {
        manager.addTask(task1);
        Task overlappingTask = new Task(
                "Overlapping Task",
                "Description",
                Status.NEW,
                Duration.ofHours(1)
        );
        overlappingTask.setStartTime(task1.getStartTime().plusHours(1));

        assertTrue(manager.hasOverlaps(overlappingTask));
    }

    @Test
    void testGetHistory() {
        manager.addTask(task1);
        manager.getTask(task1.getId());

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void testGetPriorityTasks() {
        manager.addTask(task1);
        manager.addTask(task2);

        List<Task> priorityTasks = manager.getPriorityTasks();
        assertEquals(2, priorityTasks.size());
        assertEquals(task2, priorityTasks.get(0)); // Task with earlier time should come first
    }
}

