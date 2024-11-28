package model;
import model.enums.Status;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private final int epicId;

    public SubTask(int id, String name, String description, Status status,Duration duration, int epicId) {
        super(id,name,description,status,duration);
        this.startTime = LocalDateTime.now();
        this.endTime = getEndTime();
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                '}';
    }
}