package model;
import model.enums.Status;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subTasksIds = new ArrayList<>();
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Epic(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(id,name,description,status,startTime,duration);
    }

    public Epic(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super(id, name, description, status, duration);
        this.startTime = (startTime != null) ? startTime : LocalDateTime.now();
        this.endTime = (endTime != null) ? endTime : updateEndTime();
    }

    public LocalDateTime updateEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subTasksIds=" + subTasksIds +
                ", startTime=" + endTime +
                ", endTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}
