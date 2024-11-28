package service;

import model.Epic;
import model.SubTask;
import model.Task;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.managers.InMemoryTaskManager;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;
    private final LocalDateTime startTime = LocalDateTime.now();
    private final Duration duration = Duration.ofMinutes(1);
    private final LocalDateTime endTime = startTime.plus(duration);

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void addTask_shouldAddTaskToManager() {
        Task task1 = new Task(1, "Spring Boot 1", "Spring Boot is great", Status.NEW,startTime, duration,endTime);
        Task task2 = new Task(2, "Spring Boot 2", "Spring Boot letsgo", Status.NEW,startTime , duration, endTime);
        Task task3 = new Task(3, "Spring Boot 3", "Spring boot ifelse", Status.NEW, startTime, duration, endTime);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.removeTask(task1.getId());

        List<Task> remainingTasks = taskManager.getAllTasks().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        assertEquals(remainingTasks, List.of(task2, task3), "Task1 should be removed, and Task2, Task3 should remain.");

        remainingTasks.forEach(System.out::println);
    }

    @Test
    void addEpic_shouldAddEpicToManager() {

        Epic epic = new Epic(1, "Epic1", "Description Epic1", Status.NEW, duration);

        taskManager.addEpic(epic);

        Epic retrievedEpic = taskManager.getEpic(epic.getId());
        assertNotNull(retrievedEpic, "Epic should be added.");
        assertEquals(epic.getName(), retrievedEpic.getName(), "Epic name should match.");
    }

    @Test
    void addSubtask_shouldAddSubtaskToEpic() {
        Epic epic = new Epic(2, "Epic1", "Description epic1", Status.NEW, duration);
        Epic epic2 = new Epic(3, "Epic2", "Description epic2", Status.NEW, duration);

        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);

        SubTask subTask = new SubTask(1, "Add Test for Spring Tasks", "Spring boot subtasks1", Status.NEW,duration, epic.getId());
        SubTask subTask2 = new SubTask(2, "subtask2", "subtask2 description", Status.NEW, duration, epic2.getId());

        taskManager.addSubtask(subTask);
        taskManager.addSubtask(subTask2);

        List<SubTask> subtasks = taskManager.getSubtasksOfEpic(epic.getId()).stream()
                .filter(Objects::nonNull)
                .toList();

        List<SubTask> subtasks2 = taskManager.getSubtasksOfEpic(epic2.getId()).stream()
                .filter(Objects::nonNull)
                .toList();

        assertEquals(1, subtasks.size(), "Epic 1 should contain one subtask.");
        assertTrue(subtasks.contains(subTask), "Subtask1 should be present in Epic 1.");

        assertEquals(1, subtasks2.size(), "Epic 2 should contain one subtask.");
        assertTrue(subtasks2.contains(subTask2), "Subtask2 should be present in Epic 2.");
    }

    @Test
    void updateTask_shouldUpdateExistingTask() {

        Task task = new Task(6, "Reactjs", "Reactjs great frontend", Status.NEW,startTime, duration, endTime);
        taskManager.addTask(task);

        task.setName("Updated Task");
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTask(task.getId());
        assertEquals("Updated Task", updatedTask.getName(), "Task name should be updated.");
    }

    @Test
    void removeTask_shouldRemoveTaskFromManager() {
        Task task = new Task(7, "VueJs", "Vue js better", Status.NEW, startTime,  duration, endTime);
        taskManager.addTask(task);

        taskManager.removeTask(task.getId());

        Task removedTask = taskManager.getTask(task.getId());
        assertNull(removedTask, "Task should be removed.");
    }
}



