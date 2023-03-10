package tracker.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.server.KVTaskClient;
import tracker.server.adapters.DurationDeserializer;
import tracker.server.adapters.DurationSerializer;
import tracker.server.adapters.LocalDateTimeDeserializer;
import tracker.server.adapters.LocalDateTimeSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class HttpTaskManager extends FileBackedTasksManager {

    private KVTaskClient kvClient;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(Duration.class, new DurationSerializer())
            .registerTypeAdapter(Duration.class, new DurationDeserializer())
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public HttpTaskManager(String path) {
        super(path);
        kvClient = new KVTaskClient(path);
        kvClient.register();
    }

    @Override
    public void save() {
        kvClient.put("tasks", gson.toJson(getTasks()));
        kvClient.put("epics", gson.toJson(getEpics()));
        kvClient.put("subtasks", gson.toJson(getSubTasks()));
        kvClient.put("history", gson.toJson(getHistoryManager().getHistory()));
        kvClient.put("prioritized", gson.toJson(getPrioritizedTasks()));
    }

    public void load() {

            Type type = new TypeToken<HashMap<Integer, Task>>(){}.getType();
            tasks = gson.fromJson(kvClient.load("tasks"), type);
            type = new TypeToken<HashMap<Integer, Epic>>(){}.getType();
            epics = gson.fromJson(kvClient.load("epics"), type);
            type = new TypeToken<HashMap<Integer, SubTask>>(){}.getType();
            subTasks = gson.fromJson(kvClient.load("subtasks"), type);
            type = new TypeToken<ArrayList<Task>>(){}.getType();
            ArrayList<Task> historyList = getHistoryManager().getHistory();
            historyList = gson.fromJson(kvClient.load("history"), type);

    }
}
