package model;
import model.enums.Status;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
public class Task {

    protected int id;
    protected String name;
    protected Status status;
    protected String description;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;

    public Task(int id, String name,String description, Status status,Duration duration) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = LocalDateTime.now();
        this.endTime = startTime.plus(duration);
        this.duration = duration;
    }

    public Task(int id, String name,String description, Status status,LocalDateTime startTime,Duration duration,LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = LocalDateTime.now();
        this.duration = duration;
        this.endTime = startTime.plus(duration);
    }

    public Task(String name, String description, Status status,LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = LocalDateTime.now();
        this.duration = duration;
        this.endTime = startTime.plus(duration);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime deadline) {
        this.startTime = deadline;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return id == task.id
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && status == task.status
                && Objects.equals(duration, task.duration)
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

}
