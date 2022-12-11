import java.util.ArrayList;
import java.lang.Runtime.*;

public class Epic extends Task {

    protected ArrayList<SubTask> taskList = new ArrayList<>();

    public Epic(String title, String description, String status) {
        super(title, description, status);
    }

    @Override
    public void setStatus(String status) {
        StackTraceElement[] ste = new Exception().getStackTrace();  // проверяем, откуда вызван метод, если из TaskManager,
        if (ste[1].getClassName().equals("Epic")) {         // то обновляем статус, иначе - не обновляем
            this.status = status;
        }
    }

    public void deleteAllSubtasksInCurrEpic() {
        taskList = new ArrayList<>();
        changingStatusChecker();
    }

    public void addSubtask(SubTask subtask) {
        taskList.add(subtask);
        changingStatusChecker();
    }

    public void deleteSubtask(SubTask subTask) {
        taskList.remove(subTask);
        changingStatusChecker();
    }

    public void updateSubtask(SubTask subtask) {
        int index = 0;
        for (SubTask subtask_ : taskList) {;
            if (subtask_.getId() == subtask.getId()) {
                break;
            }
            index++;
        }
        taskList.set(index, subtask);
        changingStatusChecker();
    }

    public ArrayList<SubTask> getTaskList() {
        return taskList;
    }

    public void setTaskList(ArrayList<SubTask> taskList) {
        this.taskList = taskList;
    }

    void changingStatusChecker() {
        int doneCounter = 0;
        boolean progressFlag = false;

        for (SubTask each : taskList) {
            if (each.getStatus().equals("IN_PROGRESS")) {
                setStatus("IN_PROGRESS");
                progressFlag = true;
                break;
            }
            if (each.getStatus().equals("DONE")) {
                doneCounter++;
                setStatus("IN_PROGRESS");
                progressFlag = true;
                if (doneCounter == taskList.size()) {
                    setStatus("DONE");
                    break;
                }
                continue;
            }
            setStatus("NEW");
        }
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
