package tracker.util;

import tracker.controllers.FileBackedTasksManager;
import tracker.controllers.InMemoryHistoryManager;
import tracker.controllers.InMemoryTaskManager;
import tracker.interfaces.HistoryManager;
import tracker.interfaces.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBacked() { return new FileBackedTasksManager("resources/data.csv"); }

}
