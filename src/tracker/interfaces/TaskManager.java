package tracker.interfaces;

import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.TreeSet;

public interface TaskManager {
    int getCurrent_id();

    void createTask(Task o);

    void fillEpic(Epic epic, SubTask subTask);

    void updateTask(Task o);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    void deleteSubTaskById(int id);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    Task getTaskById(int id);

    HashMap<Integer, Task> getTasks();

    void setTasks(HashMap<Integer, Task> tasks);

    HashMap<Integer, SubTask> getSubTasks();

    void setSubTasks(HashMap<Integer, SubTask> subTasks);

    HashMap<Integer, Epic> getEpics();

    void setEpics(HashMap<Integer, Epic> epics);

    HistoryManager getHistoryManager();

    TreeSet<Task> getPrioritizedTasks();

}
