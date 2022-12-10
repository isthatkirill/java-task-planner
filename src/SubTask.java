public class SubTask extends Task {

    private int epicsId;

    public SubTask(String title, String description, String status) {
        super(title, description, status);
    }

    public SubTask(String title, String description, int id, String status) {
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
        return "SubTask{" +
                "epicsId=" + epicsId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
