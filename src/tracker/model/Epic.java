package tracker.model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<SubTask> taskList = new ArrayList<>();

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    @Override
    public void setStatus(Status status) {
        StackTraceElement[] ste = new Exception().getStackTrace();
        if (ste[1].getClassName().equals("tracker.model.Epic")) {
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
        for (SubTask subtask_ : taskList) {
            ;
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

    private void changingStatusChecker() {
        int doneCounter = 0;
        boolean progressFlag = false;

        for (SubTask each : taskList) {
            if (each.getStatus().equals(Status.IN_PROGRESS)) {
                setStatus(Status.IN_PROGRESS);
                progressFlag = true;
                break;
            }
            if (each.getStatus().equals(Status.DONE)) {
                doneCounter++;
                setStatus(Status.IN_PROGRESS);
                progressFlag = true;
                if (doneCounter == taskList.size()) {
                    setStatus(Status.DONE);
                    break;
                }
                continue;
            }
            setStatus(Status.NEW);
        }
    }

    @Override
    public String toString() {
        return "tracker.model.Epic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                "taskList=" + taskList +
                '}';
    }

}
