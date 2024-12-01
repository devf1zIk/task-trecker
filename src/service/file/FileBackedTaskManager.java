package service.file;
import exception.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.Status;
import model.enums.TaskType;
import service.managers.InMemoryTaskManager;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private static final String Stroke = "id,type,name,description,status,startTime,duration,endTime,epicId\n";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(SubTask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int epicId) {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public void removeSubtask(int subtaskId) {
        super.removeSubtask(subtaskId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        save();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        save();
    }

    private void save() {
            try (Writer writer = new FileWriter(file)) {
                writer.write(Stroke);
                for (Task task : getAllTasks()) {
                    writer.write(toCSV(task) + "\n");
                }
                for (Epic epic : getAllEpics()) {
                    writer.write(toCSV(epic) + "\n");
                }
                for (SubTask subtask : getAllSubtasks()) {
                    writer.write(toCSV(subtask) + "\n");
                }
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка при сохранении в файл", e);
            }
    }

    private String toCSV(Task task) {
            String type = task instanceof Epic ? "EPIC" : (task instanceof SubTask ? "SUBTASK" : "TASK");
            String epicId = task instanceof SubTask ? String.valueOf(((SubTask) task).getEpicId()) : "";

            return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s",
                    task.getId(),
                    type,
                    task.getName(),
                    task.getDescription(),
                    task.getStatus().name(),
                    task.getStartTime(),
                    task.getDuration(),
                    task.getEndTime(),
                    epicId
            );
    }

    private Task fromCSV(String[] fields) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            int id = Integer.parseInt(fields[0]);
            TaskType type = TaskType.valueOf(fields[1].toUpperCase());
            String name = fields[2];
            Status status = Status.valueOf(fields[3]);
            String description = fields[4];
            Duration duration = fields[5].isEmpty() ? Duration.ZERO : Duration.parse(fields[5]);
            LocalDateTime startTime = fields[6].isEmpty() ? null : LocalDateTime.parse(fields[6]);
            switch (type) {
                case TASK:
                    return new Task(id,name, description, Status.NEW, startTime, duration);
                case EPIC:
                    return new Epic(id, name, description,Status.NEW);
                case SUBTASK:
                    int epicId = Integer.parseInt(fields[7]);
                    return new SubTask(id,name,description,Status.NEW,LocalDateTime.now(),Duration.ofMinutes(1),epicId);
                default:
                    throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
            }

        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка с строкой ",new Throwable("fromCSV"));
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Map<Integer, Task> tasks = new HashMap<>();
        Map<Integer, Epic> epics = new HashMap<>();
        Map<Integer, SubTask> subtasks = new HashMap<>();
        TaskType[] types = {TaskType.EPIC, TaskType.TASK, TaskType.SUBTASK};
        int id = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String lines = reader.readLine();
                if(lines.equals(Stroke) || lines.trim().isEmpty()) {
                    continue;
                }
                Task task = manager.fromCSV(lines.split(","));
                if (task.getId() >= id) {
                    tasks.put(id, task);
                }

                for (TaskType type : types) {
                    switch (type) {
                        case TASK:
                            tasks.put(id, task);
                            manager.prioritizedTasks.add(manager.getTask(id));
                            break;
                        case EPIC:
                            manager.epics.put(task.getId(),(Epic) task);
                            break;
                        case SUBTASK:
                            manager.subtasks.put(task.getId(),(SubTask) task);
                            manager.prioritizedTasks.addAll(manager.getAllSubtasks());
                            break;
                    }
                }
            }
        }catch (IOException e) {
            throw new ManagerSaveException(e.getMessage(),new Throwable("loadFromFile"));
        }
        manager.prioritizedTasks = new TreeSet<>(tasks.values());
        return manager;
    }

}
