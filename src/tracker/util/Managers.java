package tracker.util;

import tracker.controllers.HttpTaskManager;
import tracker.controllers.InMemoryHistoryManager;
import tracker.interfaces.HistoryManager;
import tracker.interfaces.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8078");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
