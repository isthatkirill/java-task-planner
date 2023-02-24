package tracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import tracker.interfaces.TaskManager;
import tracker.model.Status;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.server.adapters.DurationDeserializer;
import tracker.server.adapters.DurationSerializer;
import tracker.server.adapters.LocalDateTimeDeserializer;
import tracker.server.adapters.LocalDateTimeSerializer;
import tracker.util.Managers;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private final TaskManager taskManager = Managers.getFileBacked();

    private final int PORT = 8080;
    private final Charset DEFAULT_CHARSET = Charset.defaultCharset();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(Duration.class, new DurationSerializer())
            .registerTypeAdapter(Duration.class, new DurationDeserializer())
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    private HttpServer httpServer;

    {
        Task task = new Task("1", "1", Status.NEW);
        Task task1 = new Task("2", "2", Status.NEW,
                LocalDateTime.of(2022, 2, 2, 2, 2), Duration.ofMinutes(15));
        taskManager.createTask(task);
        taskManager.createTask(task1);
        SubTask subTask = new SubTask("3", "3", Status.NEW);
        SubTask subTask1 = new SubTask("4", "4", Status.NEW);
        taskManager.createTask(subTask);
        taskManager.createTask(subTask1);
    }

    public HttpTaskServer() {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TaskHandler());
            httpServer.start();
            System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String path = httpExchange.getRequestURI().toString();
            Endpoint endpoint = getEndpoint(path, httpExchange.getRequestMethod());
            switch (endpoint) {
                case GET_TASKS:
                    writeResponse(httpExchange, gson.toJson(taskManager.getTasks()), 200);
                    break;

                case GET_TASK_BY_ID:
                    int id = Integer.parseInt(path.split("=")[1]);
                    Task task = taskManager.getTaskById(id);
                    if (task != null && task.getClass() == Task.class) {
                        writeResponse(httpExchange, gson.toJson(taskManager.getTaskById(id)), 200);
                    } else {
                        writeResponse(httpExchange, "Задачи типа Task c id = " + id + " не существует", 404);
                    }
                    break;

                case POST_ADD_TASK:
                    String taskBody = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                    Task tempTask = gson.fromJson(taskBody, Task.class);

                    if (taskManager.getTasks().containsKey(tempTask.getId())) {
                        taskManager.updateTask(tempTask);
                        writeResponse(httpExchange, "[Task] Задача обновлена", 201);
                    } else {
                        taskManager.createTask(tempTask);
                        writeResponse(httpExchange, "[Task] Задача создана", 201);
                    }
                    break;

                case DELETE_ALL_TASKS:
                    taskManager.getTasks().clear();
                    writeResponse(httpExchange, "[Task] Задачи удалены", 201);
                    break;

                case DELETE_TASK_BY_ID:
                    int taskID = Integer.parseInt(path.split("=")[1]);
                    if (taskManager.getTasks().containsKey(taskID)) {
                        taskManager.deleteTaskById(taskID);
                        writeResponse(httpExchange, "[Task] Задача удалена", 201);
                    } else {
                        writeResponse(httpExchange, "[Task] Задачи не существует", 404);
                    }
                    break;

                case GET_SUBTASKS:
                    writeResponse(httpExchange, gson.toJson(taskManager.getSubTasks()), 200);
                    break;

                case GET_SUBTASK_BY_ID:
                    int subtaskID = Integer.parseInt(path.split("=")[1]);
                    Task subtask = taskManager.getTaskById(subtaskID);
                    if (subtask != null && subtask.getClass() == SubTask.class) {
                        writeResponse(httpExchange, gson.toJson(taskManager.getTaskById(subtaskID)), 200);
                    } else {
                        writeResponse(httpExchange, "Задачи типа SubTask c id = " + subtaskID + " не существует", 404);
                    }
                    break;

                case DELETE_ALL_SUBTASKS:
                    taskManager.getSubTasks().clear();
                    writeResponse(httpExchange, "[Subtask] Задачи удалены", 201);
                    break;


                default:
                    writeResponse(httpExchange, "Такого эндпоинта не существует", 406);
            }
        }
    }



    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 3 && pathParts[2].equals("task") && requestMethod.equals("GET")) {
            return Endpoint.GET_TASKS;
        } else if (pathParts.length == 3 && pathParts[2].startsWith("task?id=") && requestMethod.equals("GET")) {
            return Endpoint.GET_TASK_BY_ID;
        } else if (pathParts.length == 3 && pathParts[2].equals("task") && requestMethod.equals("POST")) {
            return Endpoint.POST_ADD_TASK;
        } else if (pathParts.length == 3 && pathParts[2].equals("task") && requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_ALL_TASKS;
        } else if (pathParts.length == 3 && pathParts[2].startsWith("task?id=") && requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_TASK_BY_ID;
        } else if (pathParts.length == 3 && pathParts[2].equals("subtask") && requestMethod.equals("GET")) {
            return Endpoint.GET_SUBTASKS;
        } else if (pathParts.length == 3 && pathParts[2].startsWith("subtask?id=") && requestMethod.equals("GET")) {
            return Endpoint.GET_SUBTASK_BY_ID;
        } else if (pathParts.length == 3 && pathParts[2].equals("subtask") && requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_ALL_SUBTASKS;
        }

        return Endpoint.UNKNOWN;
    }

    public void stop() {
        httpServer.stop(0);
    }
}
