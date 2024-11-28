package model;

import model.enums.Status;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subTasksIds = new ArrayList<>();

    public Epic(int id, String name, String description, Status status,Duration duration) {
        super(id, name, description, status, duration);
        this.startTime = LocalDateTime.now();
        this.endTime = startTime.plus(duration);
    }

    public List<Integer> getSubTasks() {
        return new ArrayList<>(subTasksIds);
    }

    public void addSubTask(int id) {
        subTasksIds.add(id);
    }

    public void removeSubTask(int id) {
        subTasksIds.remove(Integer.valueOf(id));
    }

    public void clearSubTasks() {
        subTasksIds.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subTasksIds=" + subTasksIds +
                ", startTime=" + endTime +
                ", endTime=" + startTime +
                ", duration=" + getDuration() +
                '}';
    }
}
