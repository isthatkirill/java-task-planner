package tracker.interfaces;

import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {
    public int getCurrent_id();

    public void createTask(Task o);

    public void fillEpic(Epic epic, SubTask subTask);

    public void updateTask(Task o);

    public void deleteAllTasks();

    public void deleteAllEpics();

    public void deleteAllSubtasks();

    public void deleteSubTaskById(int id);

    public void deleteTaskById(int id);

    public void deleteEpicById(int id);

    public Object getTaskById(int id);

    public HashMap<Integer, Task> getTasks();

    public void setTasks(HashMap<Integer, Task> tasks);

    public HashMap<Integer, SubTask> getSubTasks();

    public void setSubTasks(HashMap<Integer, SubTask> subTasks);

    public HashMap<Integer, Epic> getEpics();

    public void setEpics(HashMap<Integer, Epic> epics);

    public ArrayList<Task> getHistory();
}
