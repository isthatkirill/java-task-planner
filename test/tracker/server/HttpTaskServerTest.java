package tracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.controllers.HttpTaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.server.adapters.DurationDeserializer;
import tracker.server.adapters.DurationSerializer;
import tracker.server.adapters.LocalDateTimeDeserializer;
import tracker.server.adapters.LocalDateTimeSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class HttpTaskServerTest {

    HttpClient httpClient;
    HttpTaskServer httpTaskServer;
    HttpTaskManager hts;
    KVServer kvServer;
    private final String SERVER_PATH = "http://localhost:8080";
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
            createTestData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void afterEach() {
        httpTaskServer.stop();
        kvServer.stop();

    }

    private void createTestData() {
        hts = (HttpTaskManager) httpTaskServer.getTaskManager();
        Task task = new Task("1", "1", Status.NEW,
                LocalDateTime.of(2022, 2, 2, 3, 2), Duration.ofMinutes(35));
        Task task1 = new Task("2", "2", Status.NEW,
                LocalDateTime.of(2022, 2, 2, 2, 2), Duration.ofMinutes(15));
        hts.createTask(task);
        hts.createTask(task1);

        Epic epic = new Epic("test epic", "test", Status.NEW);
        hts.createTask(epic);

        SubTask subTask = new SubTask("3", "3", Status.NEW,
                LocalDateTime.of(2022, 12, 3, 2, 30), Duration.ofMinutes(15));
        SubTask subTask1 = new SubTask("4", "4", Status.NEW,
                LocalDateTime.of(2022, 12, 3, 4, 30), Duration.ofMinutes(25));

        hts.createTask(subTask);
        hts.createTask(subTask1);
        hts.fillEpic(epic, subTask);

        hts.getTaskById(task.getId());
    }

    @Test
    public void requestWithWrongPathShouldReturns404ResponseCode() {
        URI url = URI.create(SERVER_PATH + "/wrong/path");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int expectedStatusCode = 404;
            int actualStatusCode = response.statusCode();

            assertEquals(expectedStatusCode, actualStatusCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getTasksTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            Type type = new TypeToken<HashMap<Integer, Task>>() {
            }.getType();
            int expectedStatusCode = 200;
            int actualStatusCode = response.statusCode();
            var expectedTasks = hts.getTasks();
            var actualTasks = gson.fromJson(body, type);  //<-- HashMap.class ??

            assertEquals(expectedStatusCode, actualStatusCode);
            assertEquals(expectedTasks, actualTasks);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getTaskByIdTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            int expectedStatusCode = 200;
            int actualStatusCode = response.statusCode();
            var expectedTasks = hts.getTaskById(1);
            var actualTasks = gson.fromJson(body, Task.class);

            assertEquals(expectedStatusCode, actualStatusCode);
            assertEquals(expectedTasks, actualTasks);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getTaskByNonExistentIdTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/task?id=9999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ;

            int expectedStatusCode = 404;
            int actualStatusCode = response.statusCode();

            assertEquals(expectedStatusCode, actualStatusCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteTaskByIdTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int expectedStatusCode = 201;
            int actualStatusCode = response.statusCode();

            assertEquals(expectedStatusCode, actualStatusCode);
            getTasksTest();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteTasksTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int expectedStatusCode = 201;
            int actualStatusCode = response.statusCode();

            assertEquals(expectedStatusCode, actualStatusCode);
            getTasksTest();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSubtasksTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            Type type = new TypeToken<HashMap<Integer, SubTask>>() {
            }.getType();
            int expectedStatusCode = 200;
            int actualStatusCode = response.statusCode();
            var expectedTasks = hts.getSubTasks();
            var actualTasks = gson.fromJson(body, type);

            assertEquals(expectedStatusCode, actualStatusCode);
            assertEquals(expectedTasks, actualTasks);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSubtaskByIdTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/subtask?id=4");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            int expectedStatusCode = 200;
            int actualStatusCode = response.statusCode();
            var expectedTasks = hts.getTaskById(4);
            var actualTasks = gson.fromJson(body, SubTask.class);

            assertEquals(expectedStatusCode, actualStatusCode);
            assertEquals(expectedTasks, actualTasks);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteSubTaskByIdTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/subtask?id=4");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int expectedStatusCode = 201;
            int actualStatusCode = response.statusCode();

            assertEquals(expectedStatusCode, actualStatusCode);
            getSubtasksTest();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteSubTaskTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int expectedStatusCode = 201;
            int actualStatusCode = response.statusCode();

            assertEquals(expectedStatusCode, actualStatusCode);
            getSubtasksTest();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getEpicsTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            Type type = new TypeToken<HashMap<Integer, Epic>>() {
            }.getType();
            int expectedStatusCode = 200;
            int actualStatusCode = response.statusCode();
            var expectedTasks = hts.getEpics();
            var actualTasks = gson.fromJson(body, type);

            assertEquals(expectedStatusCode, actualStatusCode);
            assertEquals(expectedTasks, actualTasks);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getEpicByIdTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/epic?id=3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            int expectedStatusCode = 200;
            int actualStatusCode = response.statusCode();
            var expectedTasks = hts.getTaskById(3);
            var actualTasks = gson.fromJson(body, Epic.class);

            assertEquals(expectedStatusCode, actualStatusCode);
            assertEquals(expectedTasks, actualTasks);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteEpicByIdTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/epic?id=3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int expectedStatusCode = 200;
            int actualStatusCode = response.statusCode();

            assertEquals(expectedStatusCode, actualStatusCode);
            getEpicsTest();
            getSubtasksTest();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteEpicTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int expectedStatusCode = 200;
            int actualStatusCode = response.statusCode();

            assertEquals(expectedStatusCode, actualStatusCode);
            getSubtasksTest();
            getEpicsTest();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getEpicsSubtasksTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/subtask/epic?id=3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            Type type = new TypeToken<ArrayList<SubTask>>() {
            }.getType();
            int expectedStatusCode = 200;
            int actualStatusCode = response.statusCode();
            var expectedTasks = hts.getEpics().get(3).getTaskList();
            var actualTasks = gson.fromJson(body, type);

            assertEquals(expectedStatusCode, actualStatusCode);
            assertEquals(expectedTasks, actualTasks);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getHistoryTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            Type type = new TypeToken<ArrayList<Task>>() {
            }.getType();
            int expectedStatusCode = 200;
            int actualStatusCode = response.statusCode();
            var expectedHistory = hts.getHistoryManager().getHistory();
            var actualHistory = gson.fromJson(body, type);

            assertEquals(expectedStatusCode, actualStatusCode);
            assertEquals(expectedHistory, actualHistory);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getPrioritizedTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            Type type = new TypeToken<List<Task>>() {
            }.getType();
            int expectedStatusCode = 200;
            int actualStatusCode = response.statusCode();
            var expectedHistory = new ArrayList<>(hts.getPrioritizedTasks());
            List<Task> actualHistory = gson.fromJson(body, type);

            assertEquals(expectedStatusCode, actualStatusCode);
            for (int i = 0; i < expectedHistory.size(); i++) {
                assertEquals(expectedHistory.get(i).getId(), actualHistory.get(i).getId());
                assertEquals(expectedHistory.get(i).getTitle(), actualHistory.get(i).getTitle());
                assertEquals(expectedHistory.get(i).getStartTime(), actualHistory.get(i).getStartTime());
                assertEquals(expectedHistory.get(i).getDuration(), actualHistory.get(i).getDuration());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addTaskTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/task");
        Task task = new Task("new task", "desc", Status.NEW);
        HttpRequest.BodyPublisher bp = HttpRequest.BodyPublishers.ofString(gson.toJson(task));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(bp)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            int expectedStatusCode = 201;
            int actualStatusCode = response.statusCode();

            assertEquals(expectedStatusCode, actualStatusCode);
            getTasksTest();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addSubtaskTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/subtask");
        SubTask subtask = new SubTask("new subtask", "desc", Status.NEW);
        HttpRequest.BodyPublisher bp = HttpRequest.BodyPublishers.ofString(gson.toJson(subtask));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(bp)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int expectedStatusCode = 201;
            int actualStatusCode = response.statusCode();

            assertEquals(expectedStatusCode, actualStatusCode);
            getSubtasksTest();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addEpicTest() {
        URI url = URI.create(SERVER_PATH + "/tasks/epic");
        Epic epic = new Epic("new epic", "desc", Status.NEW);
        HttpRequest.BodyPublisher bp = HttpRequest.BodyPublishers.ofString(gson.toJson(epic));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(bp)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int expectedStatusCode = 201;
            int actualStatusCode = response.statusCode();

            assertEquals(expectedStatusCode, actualStatusCode);
            getEpicsTest();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
