package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import service.managers.InMemoryTaskManager;
import java.util.List;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager manager;
    private Task task;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void testAddTask() {
        manager.addTask(task);
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals(tasks, tasks.get(0));
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
        manager.addTask(task);
        task.setName("Updated Task 1");
        manager.updateTask(task);

        Task updatedTask = manager.getTask(task.getId());
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
        manager.addTask(task);
        manager.removeTask(task.getId());

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
        manager.addTask(task);
        Task overlappingTask = new Task(
                "Overlapping Task",
                "Description",
                Status.NEW,
                Duration.ofHours(1)
        );
        overlappingTask.setStartTime(task.getStartTime().plusHours(1));

        assertTrue(manager.hasOverlaps(overlappingTask));
    }

    @Test
    void testGetHistory() {
        manager.addTask(task);
        manager.getTask(task.getId());

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void testGetPriorityTasks() {
        manager.addTask(task);
        manager.addTask(task);

        List<Task> priorityTasks = manager.getPriorityTasks();
        assertEquals(2, priorityTasks.size());
        assertEquals(task, priorityTasks.get(0));
    }
}

