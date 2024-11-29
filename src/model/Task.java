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
    protected LocalDateTime startTime = LocalDateTime.now();
    protected Duration defaultDuration = Duration.ZERO;

    public Task(int id, String name,String description, Status status,Duration duration) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = defaultDuration;
    }

    public Task(int id,String name, String description, Status status,LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = defaultDuration;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return id == task.id;
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
