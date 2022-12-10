import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int current_id = 0;

    public int getCurrent_id() {
        return current_id;
    }

    public void createTask(Object o) {
        if ((o != null) && o.getClass() == Task.class) {
            Task task = (Task) o;
            task.setId(++current_id);
            tasks.put(current_id, task);
        } else if ((o != null) && o.getClass() == SubTask.class) {
            SubTask subTask = (SubTask) o;
            subTask.setId(++current_id);
            subTasks.put(current_id, subTask);
        } else if ((o != null) && o.getClass() == Epic.class) {
            Epic epic = (Epic) o;
            epic.setId(++current_id);
            epics.put(current_id, epic);
        }
    }

    public void fillEpic(Epic epic, SubTask subTask) {
        subTask.setEpicsId(epic.getId());
        epic.taskList.add(subTask);
    }

    public void updateTask(Object o) {
        if ((o != null) && o.getClass() == Task.class) {
            Task task = (Task) o;
            tasks.put(task.getId(), task);;
        } else if ((o != null) && o.getClass() == SubTask.class) {
            SubTask subTask = (SubTask) o;
            subTasks.put(subTask.getId(), subTask);

            for (Epic epic : epics.values()) {
                ArrayList<SubTask> temp = epic.getTaskList();
                for (SubTask subtask_ : temp) {
                    if (subTask.getId() == subtask_.getId()) {
                        subTask.setEpicsId(subtask_.getEpicsId());
                    }
                }

            }

            Epic epic = epics.get(subTask.getEpicsId());
            ArrayList<SubTask> temp = epic.getTaskList();

            for (SubTask currTask : temp) {
                if (currTask.getId() == subTask.getId()) {
                    temp.set(temp.indexOf(currTask), subTask);
                }
            }
            epic.setTaskList(temp);
            changingStatusChecker(epic);
        } else if ((o != null) && o.getClass() == Epic.class) {
            Epic epic = (Epic) o;
            epics.put(epic.getId(), epic);
        }
    }

    public void deleteAllTasks() {
            tasks = new HashMap<>();
    }

    public void deleteAllEpics() {
            epics = new HashMap<>();
            subTasks = new HashMap<>();
    }

    public void deleteAllSubtasks(Epic epic) {
            subTasks = new HashMap<>();
            epic.setTaskList(new ArrayList<>());
    }

    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicsId());
        ArrayList<SubTask> temp = epic.getTaskList();

        for (SubTask currTask : temp) {
            if (currTask.getId() == subTask.getId()) {
                temp.remove(currTask);
                break;
            }
        }
        epic.setTaskList(temp);
        subTasks.remove(id);

        changingStatusChecker(epic);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        epics.remove(id);
    }

    public Object getTaskById(int id) {
        if (tasks.get(id) != null) { return tasks.get(id); }
        if (subTasks.get(id) != null) { return subTasks.get(id); }
        return null;
    }

    void changingStatusChecker(Epic epic) {
        ArrayList<SubTask> temp = epic.getTaskList();
        int doneCounter = 0;
        boolean progressFlag = false;

        for (SubTask each : temp) {
            if (each.getStatus().equals("IN_PROGRESS")) {
                epic.setStatus("IN_PROGRESS");
                progressFlag = true;
                continue;
            }
            if (each.getStatus().equals("DONE")) {
                doneCounter++;
                epic.setStatus("IN_PROGRESS");
                progressFlag = true;
                if (doneCounter == temp.size()) {
                    epic.setStatus("DONE");
                    break;
                }
            }

            if (!progressFlag) {
                epic.setStatus("NEW");
            }
        }
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public void setTasks(HashMap<Integer, Task> tasks) {
        this.tasks = tasks;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(HashMap<Integer, SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public void setEpics(HashMap<Integer, Epic> epics) {
        this.epics = epics;
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "tasks=" + tasks +
                ", subTasks=" + subTasks +
                ", epics=" + epics +
                ", current_id=" + current_id +
                '}';
    }
}
