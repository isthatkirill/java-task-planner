package tracker.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class Epic extends Task {

    private ArrayList<SubTask> taskList = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    public Epic(String title, int id, String description, Status status) {
        super(title, description, id, status);
    }

    public ArrayList<SubTask> getTaskList() {
        return taskList;
    }

    public void setTaskList(ArrayList<SubTask> taskList) {
        this.taskList = taskList;
        changingStartTimeAndEndTimeChecker();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
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
        changingStartTimeAndEndTimeChecker();
    }

    private void changingStartTimeAndEndTimeChecker() {
         taskList.stream()
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .ifPresent(localDateTime -> endTime = localDateTime);

        taskList.stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .ifPresent(localDateTime -> startTime = localDateTime);
    }


    public void deleteSubtask(SubTask subTask) {
        taskList.remove(subTask);
        changingStatusChecker();
    }

    public void updateSubtask(SubTask subtask) {
        int index = 0;
        for (SubTask subtask_ : taskList) {
            if (subtask_.getId() == subtask.getId()) {
                break;
            }
            index++;
        }
        taskList.set(index, subtask);
        changingStatusChecker();
        changingStartTimeAndEndTimeChecker();
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
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", taskList=" + taskList +
                '}';
    }
}
