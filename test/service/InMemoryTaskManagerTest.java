package service;

import model.Epic;
import model.enums.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.managers.InMemoryTaskManager;
import service.managers.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void addTaskTest() {
        Task task = new Task("Задача 1",Status.NEW, "Task 1");
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTask(task.getId());
        assertNotNull(retrievedTask, "Задача должна быть получена.");
        assertEquals(task, retrievedTask, "Задача должна быть получена правильно.");
    }

    @Test
    public void addEpicTest() {
        Epic epic = new Epic(1,"Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        Epic retrievedEpic = taskManager.getEpic(epic.getId());
        assertNotNull(retrievedEpic, "Эпик должен быть получен.");
        assertEquals(epic, retrievedEpic, "Эпик должен быть получен правильно.");
    }

    @Test
    public void addSubTaskTest() {
        Epic epic = new Epic(1,"Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        SubTask subtask = new SubTask("Подзадача 1", "Описание подзадачи 1",epic.getId());
        taskManager.addSubtask(subtask);

        SubTask retrievedSubtask = taskManager.getSubtask(subtask.getId());
        assertNotNull(retrievedSubtask, "Подзадача должна быть получена.");
        assertEquals(subtask, retrievedSubtask, "Подзадача должна быть получена правильно.");

        assertTrue(epic.getSubTasks().contains(subtask.getId()), "Подзадача должна быть связана с эпиком.");
    }

    @Test
    public void testTaskUpdate() {
        Task task = new Task("Задача 1", Status.NEW,"Описание задачи 1");
        taskManager.addTask(task);

        Task updatedTask = new Task(task.getId(), "Обновленная задача", "Обновленное описание", Status.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        Task retrievedTask = taskManager.getTask(task.getId());
        assertNotNull(retrievedTask, "Обновленная задача должна быть получена.");
        assertEquals(updatedTask, retrievedTask, "Обновленная задача должна быть получена правильно.");
    }

    @Test
    public void testEpicUpdate() {
        Epic epic = new Epic(2,"Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        Epic updatedEpic = new Epic(epic.getId(), "Обновленный эпик", "Обновленное описание");
        taskManager.updateEpic(updatedEpic);

        Epic retrievedEpic = taskManager.getEpic(epic.getId());
        assertNotNull(retrievedEpic, "Обновленный эпик должен быть получен.");
        assertEquals(updatedEpic, retrievedEpic, "Обновленный эпик должен быть получен правильно.");
    }

    @Test
    public void testRemoveTask() {
        Task task = new Task("Задача 1", Status.NEW,"Описание задачи 1");
        taskManager.addTask(task);

        taskManager.removeTask(task.getId());

        assertNull(taskManager.getTask(task.getId()), "Задача должна быть удалена.");
    }

    @Test
    public void testRemoveEpic() {
        Epic epic = new Epic(3,"Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        SubTask subtask = new SubTask("Подзадача 1", "Описание Подзадачи 1",epic.getId());
        taskManager.addSubtask(subtask);

        taskManager.removeEpic(epic.getId());

        assertNull(taskManager.getEpic(epic.getId()), "Эпик должен быть удален.");
        assertNull(taskManager.getSubtask(subtask.getId()), "Подзадача должна быть удалена.");
    }

    @Test
    public void testRemoveSubtask() {
        Epic epic = new Epic(4,"Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        SubTask subtask = new SubTask("Подзадача 1", "Описание Подзадачи 1", epic.getId());
        taskManager.addSubtask(subtask);

        taskManager.removeSubtask(subtask.getId());

        assertNull(taskManager.getSubtask(subtask.getId()), "Подзадача должна быть удалена.");
    }

    @Test
    public void testSubtaskIntegrityAfterRemoval() {
        Epic epic = new Epic(5,"Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        SubTask subtask = new SubTask("Подзадача 1", "Описание Подзадачи 1", epic.getId());
        taskManager.addSubtask(subtask);

        taskManager.removeSubtask(subtask.getId());

        assertFalse(epic.getSubTasks().contains(subtask.getId()), "Эпик не должен содержать удаленную подзадачу.");
    }

    @Test
    public void testUpdateTaskUpdatesManagerData() {
        Task task = new Task("Задача 1", Status.NEW, "test1");
        taskManager.addTask(task);

        task.setName("Измененная задача");
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTask(task.getId());
        assertEquals("Измененная задача", updatedTask.getName(), "Имя задачи должно быть обновлено.");
    }

}
