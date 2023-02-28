package tracker.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.interfaces.TaskManagerTest;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private HttpClient httpClient;
    private KVServer kvServer;

    private HttpTaskServer httpTaskServer;
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
            taskManager = httpTaskServer.getTaskManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            List<Integer> allowableStatusCodes = new ArrayList<>(List.of(200, 204));

            int actualStatusCode = response.statusCode();
            var expectedTaskList = taskManager.getTasks();
            HttpTaskManager hts = (HttpTaskManager) httpTaskServer.getTaskManager();
            hts.load();
            var actualTaskList = hts.getTasks();

            assertTrue(allowableStatusCodes.contains(actualStatusCode));
            assertEquals(expectedTaskList, actualTaskList);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void unauthorizedTest() {
        URI url = URI.create(SERVER_PATH + "/save/tasks?API_TOKEN=213123");
        HttpRequest.BodyPublisher bp = HttpRequest.BodyPublishers.ofString(gson.toJson(taskManager.getTasks()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(bp)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int expectedStatusCode = 403;
            int actualStatusCode = response.statusCode();
            assertEquals(expectedStatusCode, actualStatusCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void withoutKeyTest() {
        URI url = URI.create(SERVER_PATH + "/save/?API_TOKEN=" + kvServer.getApiToken());
        HttpRequest.BodyPublisher bp = HttpRequest.BodyPublishers.ofString(gson.toJson(taskManager.getTasks()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(bp)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int expectedStatusCode = 400;
            int actualStatusCode = response.statusCode();

            assertEquals(expectedStatusCode, actualStatusCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void afterEach() {
        kvServer.stop();
        httpTaskServer.stop();
    }
}