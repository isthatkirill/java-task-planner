package tracker.controllers;

import tracker.interfaces.HistoryManager;
import tracker.interfaces.TaskManager;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.util.Managers;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int current_id = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void createTask(Task o) {
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

    @Override
    public void fillEpic(Epic epic, SubTask subTask) {
        subTask.setEpicsId(epic.getId());
        epic.addSubtask(subTask);
    }

    @Override
    public void updateTask(Task o) {
        if ((o != null) && o.getClass() == Task.class) {
            Task task = (Task) o;
            tasks.put(task.getId(), task);

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
                    epic.updateSubtask(subTask);
                    break;
                }
            }
        } else if ((o != null) && o.getClass() == Epic.class) {
            Epic epic = (Epic) o;
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void deleteAllTasks() {
        for (Integer task : tasks.keySet()) {
            historyManager.remove(tasks.get(task).getId());
        }
        tasks = new HashMap<>();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        for (Integer epic : epics.keySet()) {
            historyManager.remove(epics.get(epic).getId());
        }
        epics = new HashMap<>();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtasksInCurrEpic();
        }
        for (Integer subtask : subTasks.keySet()) {
            historyManager.remove(subTasks.get(subtask).getId());
        }
        subTasks = new HashMap<>();
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicsId());

        epic.deleteSubtask(subTask);
        subTasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic currEpic = epics.get(id);
        ArrayList<SubTask> temp = currEpic.getTaskList();
        for (SubTask each : temp) {
            if (subTasks.containsKey(each.getId())) {
                subTasks.remove(each.getId());
                historyManager.remove(each.getId());
            }
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public Object getTaskById(int id) {
        if (tasks.get(id) != null) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else if (subTasks.get(id) != null) {
            historyManager.add(subTasks.get(id));
            return subTasks.get(id);
        } else if (epics.get(id) != null) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else {
            return null;
        }

    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public void setTasks(HashMap<Integer, Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public void setSubTasks(HashMap<Integer, SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public void setEpics(HashMap<Integer, Epic> epics) {
        this.epics = epics;
    }

    @Override
    public int getCurrent_id() {
        return current_id;
    }

    @Override
    public String toString() {
        return "tracker.interfaces.TaskManager{" +
                "tasks=" + tasks +
                ", subTasks=" + subTasks +
                ", epics=" + epics +
                ", current_id=" + current_id +
                '}';
    }
}
