package tracker.model;

import tracker.model.Task;

public class SubTask extends Task {

    private int epicsId;

    public SubTask(String title, String description, Status status) {
        super(title, description, status);
    }

    public SubTask(String title, String description, int id, Status status) {
        super(title, description, id, status);
    }

    public int getEpicsId() {
        return epicsId;
    }

    public void setEpicsId(int epicsId) {
        this.epicsId = epicsId;
    }

    @Override
    public String toString() {
        return "tracker.model.SubTask{" +
                "epicsId=" + epicsId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
