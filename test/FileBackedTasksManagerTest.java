import org.junit.jupiter.api.BeforeEach;
import tracker.controllers.FileBackedTasksManager;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager("resources/data.csv");
    }

    
}
