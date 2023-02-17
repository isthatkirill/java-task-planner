package tracker.controllers;

import org.junit.jupiter.api.BeforeEach;
import tracker.interfaces.TaskManagerTest;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

}
