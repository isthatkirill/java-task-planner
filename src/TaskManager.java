import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int current_id = 0;

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
            Epic epic = epics.get(subTask.getEpicsId());

            ArrayList<SubTask> temp = epic.getTaskList();
            ArrayList<SubTask> ok = new ArrayList<>();

            for (SubTask each : temp) {
                if (each.getId() != subTask.getId()) {
                    /*System.out.println(each.getId() + subTask.getId());*/
                    ok.add(each);
                } else {
                    ok.add(subTask);
                }
            }
           //epic.setTaskList(temp);



        } else if ((o != null) && o.getClass() == Epic.class) {
            Epic epic = (Epic) o;
            epics.put(epic.getId(), epic);
        }
    }

    public void deleteAllTasks(Object o) {
        if ((o != null) && o.getClass() == Task.class) {
            Task task = (Task) o;
            tasks = new HashMap<>();
        } else if ((o != null) && o.getClass() == SubTask.class) {
            SubTask subTask = (SubTask) o;
            subTasks = new HashMap<>();
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
        subTasks.remove(id);
    }

    public Object getTaskById(int id) {
        if (tasks.get(id) != null) { return tasks.get(id); }
        if (subTasks.get(id) != null) { return subTasks.get(id); }
        return null;
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
