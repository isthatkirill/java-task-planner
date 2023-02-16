package tracker.controllers;

import tracker.interfaces.HistoryManager;
import tracker.interfaces.TaskManager;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.util.Managers;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final Comparator<Task> comparator = (o1, o2) -> {
        if (o1.getStartTime() == null) {
            return 1;
        } else if (o2.getStartTime() == null) {
            return -1;
        }
        return o1.getStartTime().compareTo(o2.getStartTime());
    };

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected TreeSet<Task> taskByTime = new TreeSet<>(comparator);
    protected int current_id = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd, HH:mm");

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void createTask(Task o) {
        if ((o != null) && o.getClass() == Task.class) {
            o.setId(++current_id);
            if (isCrossing(o)) {
                o.setDuration(null);
                o.setStartTime(null);
                taskByTime.add(o);
            } else {
                taskByTime.add(o);
            }
            tasks.put(current_id, o);
        } else if ((o != null) && o.getClass() == SubTask.class) {
            SubTask subTask = (SubTask) o;
            subTask.setId(++current_id);
            if (isCrossing(o)) {
                o.setDuration(null);
                o.setStartTime(null);
                taskByTime.add(o);
            } else {
                taskByTime.add(o);
            }
            subTasks.put(current_id, subTask);
        } else if ((o != null) && o.getClass() == Epic.class) {
            Epic epic = (Epic) o;
            epic.setId(++current_id);
            epics.put(current_id, epic);
        }
    }

    @Override
    public boolean isCrossing(Task task) {
        if (task.getStartTime() != null && task.getDuration() != null) {
            return !taskByTime.stream()
                    .filter(t -> t.getStartTime() != null && t.getEndTime() != null)
                    .allMatch(t -> ((task.getStartTime().isBefore(t.getStartTime()) &&
                            task.getEndTime().isBefore(t.getStartTime())) ||
                            (task.getStartTime().isAfter(t.getEndTime()) &&
                                    task.getEndTime().isAfter(t.getEndTime()))));
        }
        return true;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return taskByTime;
    }

    @Override
    public void fillEpic(Epic epic, SubTask subTask) {
        subTask.setEpicsId(epic.getId());
        epic.addSubtask(subTask);
    }

    @Override
    public void updateTask(Task o) {
        if ((o != null) && o.getClass() == Task.class) {
            if (isCrossing(o)) {
                o.setStartTime(null);
                o.setDuration(null);
            } else {
                tasks.put(o.getId(), o);
            }

            taskByTime = taskByTime.stream()
                    .filter(t -> t.getId() != o.getId())
                    .collect(Collectors.toCollection(() ->
                            new TreeSet<>(comparator)));
            taskByTime.add(o);

        } else if ((o != null) && o.getClass() == SubTask.class) {
            SubTask subTask = (SubTask) o;
            if (isCrossing(subTask)) {
                subTask.setStartTime(null);
                subTask.setDuration(null);
            } else {
                subTasks.put(subTask.getId(), subTask);
            }

            taskByTime = taskByTime.stream()
                    .filter(t -> t.getId() != subTask.getId())
                    .collect(Collectors.toCollection(() ->
                            new TreeSet<>(comparator)));
            taskByTime.add(subTask);

            epics.values().stream()
                    .flatMap(sub -> sub.getTaskList().stream())
                    .filter(sub -> subTask.getId() == sub.getId())
                    .forEach(sub -> {
                        subTask.setEpicsId(sub.getEpicsId());
                    });


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
        tasks.keySet().forEach(t -> historyManager.remove(tasks.get(t).getId()));
        tasks = new HashMap<>();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        epics.keySet().forEach(e -> historyManager.remove(epics.get(e).getId()));
        epics = new HashMap<>();
    }

    @Override
    public void deleteAllSubtasks() {
        epics.values().forEach(Epic::deleteAllSubtasksInCurrEpic);
        subTasks.keySet().forEach(s -> historyManager.remove(subTasks.get(s).getId()));
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
        temp.stream().filter(t -> subTasks.containsKey(t.getId()))
                .forEach(t -> {
                    subTasks.remove(t.getId());
                    historyManager.remove(t.getId());
                });
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public Task getTaskById(int id) {
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
    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
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
