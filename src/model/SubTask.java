package model;

import model.enums.Status;

public class SubTask extends Task {
    private final int epicId;


    public SubTask(int id,String name, String description, Status status, int epicId) {
        super(id,name, description, status);
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
                '}';
    }
}