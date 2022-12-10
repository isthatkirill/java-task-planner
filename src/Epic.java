import java.util.ArrayList;

public class Epic extends Task{

    protected ArrayList<SubTask> taskList = new ArrayList<>();

    public Epic(String title, String description, String status) {
        super(title, description, status);
    }

    public ArrayList<SubTask> getTaskList() {
        return taskList;
    }

    public void setTaskList(ArrayList<SubTask> taskList) {
        this.taskList = taskList;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                "taskList=" + taskList +
                '}';
    }


}
