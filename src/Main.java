import com.google.gson.Gson;
import tracker.controllers.HttpTaskManager;
import tracker.controllers.InMemoryTaskManager;
import tracker.interfaces.TaskManager;
import tracker.model.Status;
import tracker.model.Task;
import tracker.server.HttpTaskServer;
import tracker.server.KVServer;
import tracker.server.KVTaskClient;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        new KVServer().start();
        new HttpTaskServer().start();
    }
}
