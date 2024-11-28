package service.managers;

import exception.ValidationException;
import model.Epic;
import model.enums.Status;
import model.SubTask;
import model.Task;
import service.Managers;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subtasks = new HashMap<>();
    protected int id = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public SubTask getSubtask(int id) {
        SubTask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void addTask(Task task) {
        task.setId(++id);
        checkTaskTime(task);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(++id);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(SubTask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtask.setId(++id);
            subtasks.put(subtask.getId(), subtask);
            epic.addSubTask(subtask.getId());
            updateStatus(epic);
        } else {
            System.out.println("Epic with ID " + subtask.getEpicId() + " not found.");
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            checkTaskTime(task);
            hasOverlaps(task);
        } else {
            System.out.println("Task with ID " + task.getId() + " not found.");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic savedEpic = epics.get(epic.getId());
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        } else {
            System.out.println("Epic with ID " + epic.getId() + " not found.");
        }
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateStatus(epic);
            }
        } else {
            System.out.println("Subtask with ID " + subtask.getId() + " not found.");
        }
    }

    @Override
    public void updateStatus(Epic epic) {
        List<SubTask> subTasks = getSubtasksOfEpic(epic.getId());

        if (subTasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean hasInProgress = false;
        boolean hasNotDone = false;

        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() == Status.IN_PROGRESS) {
                hasInProgress = true;
            } else if (subTask.getStatus() != Status.DONE) {
                hasNotDone = true;
            }
        }

        if (hasInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else if (!hasNotDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void removeTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            historyManager.remove(id);
            for (Epic epic : epics.values()) {
                epic.removeSubTask(id);
            }
        }
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubTasks()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtask(int id) {
        SubTask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubTask(id);
                updateStatus(epic);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Integer epicId : epics.keySet()) {
            Epic epic = epics.get(epicId);
            if (epic != null) {
                for (Integer subtaskId : epic.getSubTasks()) {
                    historyManager.remove(subtaskId);
                }
            }
            historyManager.remove(epicId);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            updateStatus(epic);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public List<SubTask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            List<SubTask> result = new ArrayList<>();
            for (Integer subTaskId : epic.getSubTasks()) {
                SubTask subTask = subtasks.get(subTaskId);
                if (subTask != null) {
                    result.add(subTask);
                }
            }
            return result;
        }
        return new ArrayList<>();
    }

    @Override
    public List<Task> getPriorityTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean hasOverlaps(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration().isZero()) return false;
        LocalDateTime startTime = newTask.getStartTime();
        LocalDateTime endTime = newTask.getEndTime();

        return prioritizedTasks.stream()
                .filter(task -> task.getStartTime() != null)
                .anyMatch(task -> {
                    LocalDateTime existingStart = task.getStartTime();
                    LocalDateTime existingEnd = task.getEndTime();
                    return startTime.isBefore(existingEnd) && endTime.isAfter(existingStart);
                });
    }

    private void checkTaskTime(Task task) {
        if (task.getStartTime() == null || task.getDuration().isZero()) return;

        for (Task t : prioritizedTasks) {
            if (t.getId() == task.getId()) continue;
            if (t.getStartTime() != null && t.getEndTime().isAfter(task.getStartTime()) && t.getStartTime().isBefore(task.getEndTime())) {
                throw new ValidationException("Task overlaps with an existing task.");
            }
        }
    }

}
