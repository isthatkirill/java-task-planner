import java.util.ArrayList;
import java.lang.Runtime.*;

public class Epic extends Task {

    protected ArrayList<SubTask> taskList = new ArrayList<>();

    public Epic(String title, String description, String status) {
        super(title, description, status);
    }

    @Override
    public void setStatus(String status) {
        StackTraceElement[] ste = new Exception().getStackTrace(); // проверяем, откуда вызван метод, если из TaskManager,
        if (ste[1].getClassName().equals("TaskManager")) {         // то обновляем статус, иначе - не обновляем
            this.status = status;
        }
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
