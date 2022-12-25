package tracker.util;

import tracker.controllers.InMemoryTaskManager;
import tracker.interfaces.TaskManager;

public class Managers {
    private static final TaskManager taskManager = new InMemoryTaskManager();

    public static TaskManager getDefault() {
        return taskManager;
    }
}
