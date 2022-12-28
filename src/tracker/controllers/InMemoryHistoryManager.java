package tracker.controllers;

import tracker.interfaces.HistoryManager;
import tracker.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> viewedTasks = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory() {
        return viewedTasks;
    }

    @Override
    public void add(Task task) {
        if (viewedTasks.size() == 10) {
            viewedTasks.remove(0);
        }
        viewedTasks.add(task);
    }
}
