package tracker.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Task;
import tracker.server.HttpTaskServer;
import tracker.server.KVServer;
import tracker.server.adapters.DurationDeserializer;
import tracker.server.adapters.DurationSerializer;
import tracker.server.adapters.LocalDateTimeDeserializer;
import tracker.server.adapters.LocalDateTimeSerializer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest {

    HttpClient httpClient;
    KVServer kvServer;
    InMemoryTaskManager taskManager;
    HttpTaskServer httpTaskServer;
    private final String SERVER_PATH = "http://localhost:8078";
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(Duration.class, new DurationSerializer())
            .registerTypeAdapter(Duration.class, new DurationDeserializer())
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    @BeforeEach
    public void beforeEach() {
        try {
            httpClient = HttpClient.newHttpClient();
            kvServer = new KVServer();
            kvServer.start();
            httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
            createTestdata();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createTestdata() {
        taskManager = new InMemoryTaskManager();
        Task task = new Task("1", "1", Status.NEW,
                LocalDateTime.of(2022, 2, 2, 3, 2), Duration.ofMinutes(35));
        Task task1 = new Task("2", "2", Status.NEW,
                LocalDateTime.of(2022, 2, 2, 2, 2), Duration.ofMinutes(15));
        taskManager.createTask(task);
        taskManager.createTask(task1);
    }

    @Test
    public void loadTest() {
        URI url = URI.create(SERVER_PATH + "/save/tasks?API_TOKEN=" + kvServer.getApiToken());
        HttpRequest.BodyPublisher bp = HttpRequest.BodyPublishers.ofString(gson.toJson(taskManager.getTasks()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(bp)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int expectedStatusCode = 200;
            int actualStatusCode = response.statusCode();
            var expectedTaskList = taskManager.getTasks();
            HttpTaskManager hts = (HttpTaskManager) httpTaskServer.getTaskManager();
            hts.load();
            var actualTaskList = hts.getTasks();

            assertEquals(expectedStatusCode, actualStatusCode);
            assertEquals(expectedTaskList, actualTaskList);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void afterEach() {
        kvServer.stop();
    }
}