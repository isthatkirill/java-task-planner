package tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private int epicsId;

    public SubTask(String title, String description, Status status) {
        super(title, description, status);
    }

    public SubTask(String title, String description, int id, Status status) {
        super(title, description, id, status);
    }

    public SubTask(String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(title, description, status, startTime, duration);
    }

    public int getEpicsId() {
        return epicsId;
    }

    public void setEpicsId(int epicsId) {
        this.epicsId = epicsId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicsId=" + epicsId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}
