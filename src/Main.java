import com.google.gson.Gson;
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
        //HttpTaskServer hts = new HttpTaskServer();
        Gson gson = new Gson();

        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("1", "1", Status.NEW);
        taskManager.createTask(task);


        KVServer kv = new KVServer();
        kv.start();
        KVTaskClient kvTaskClient = new KVTaskClient("http://localhost:8080");
        kvTaskClient.register();
        kvTaskClient.put("taskm", gson.toJson(taskManager.getTasks()));
        kvTaskClient.put("taskm", "1");
        kvTaskClient.put("2", "3");
        kvTaskClient.load("taskm");
        kvTaskClient.load("2");


    }
}
