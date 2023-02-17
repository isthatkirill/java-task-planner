package tracker.interfaces;

import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.SubTask;
import tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    @Test
    public void createTaskTest() {
        Task task = new Task("Task", "test task", Status.NEW);
        taskManager.createTask(task);
        final Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают");

        final HashMap<Integer, Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    public void createSubtaskTest() {
        SubTask subTask = new SubTask("SubTask", "test subTask", Status.NEW);
        taskManager.createTask(subTask);
        final Task savedTask = taskManager.getTaskById(subTask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subTask, savedTask, "Задачи не совпадают");

        final HashMap<Integer, SubTask> subtasks = taskManager.getSubTasks();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subTask, subtasks.get(1), "Задачи не совпадают.");
    }

    @Test
    public void getTaskByIdTest() {
        Task task = new Task("Task", "test task", Status.NEW);
        taskManager.createTask(task);

        Epic epic = new Epic("Epic", "test epic", Status.NEW);
        taskManager.createTask(epic);

        SubTask subTask = new SubTask("SubTask", "test subTask", Status.NEW);
        taskManager.createTask(subTask);
        taskManager.fillEpic(epic, subTask);

        Task taskById = taskManager.getTaskById(-1000);
        assertNull(taskById, "Некорректная задача");
        taskById = taskManager.getTaskById(task.getId());
        assertEquals(task, taskById, "Некорректная задача");
        taskById = taskManager.getTaskById(subTask.getId());
        assertEquals(subTask, taskById, "Некорректная задача");
        taskById = taskManager.getTaskById(epic.getId());
        assertEquals(epic, taskById, "Некорректная задача");
    }

    @Test
    public void createEpicTest() {
        Epic epic = new Epic("Epic", "test epic", Status.NEW);
        taskManager.createTask(epic);
        final Task savedTask = taskManager.getTaskById(epic.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают");

        final HashMap<Integer, Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(1), "Задачи не совпадают.");
    }

    @Test
    public void correctCurrentIdTest() {
        Task task = new Task("Task", "test task", Status.NEW);
        Task task2 = new Task("Task2", "test task2", Status.NEW);
        taskManager.createTask(task);
        taskManager.createTask(task2);
        assertEquals(2, taskManager.getTasks().size(), "Размеры коллекций не совпадают");
        assertEquals(2, taskManager.getCurrent_id());
    }

    @Test
    public void updateTaskTest() {
        Task task = new Task("Task", "test task", Status.NEW);
        taskManager.createTask(task);
        task = new Task("Task", "Updated test task", task.getId(), Status.IN_PROGRESS,
                LocalDateTime.of(2020, 10, 2, 9, 55, 30), Duration.ofMinutes(20));
        taskManager.updateTask(task);

        final Task updatedTask = taskManager.getTaskById(task.getId());

        assertNotNull(updatedTask, "Задача не найдена.");
        assertEquals(task, updatedTask, "Задачи не совпадают");
    }

    @Test
    public void updateEpicTest() {
        Epic epic = new Epic("Epic", "test epic", Status.NEW);
        taskManager.createTask(epic);
        epic = new Epic("Epic", epic.getId(), "Updated test epic", Status.NEW);
        taskManager.updateTask(epic);

        final Task updatedEpic = taskManager.getTaskById(epic.getId());

        assertNotNull(updatedEpic, "Задача не найдена.");
        assertEquals(epic, updatedEpic, "Задачи не совпадают");
    }

    @Test
    public void fillEpicTest() {
        Epic epic = new Epic("Epic", "test epic", Status.NEW);
        taskManager.createTask(epic);

        SubTask subTask = new SubTask("Subtask", "test subtask", Status.NEW);
        taskManager.createTask(subTask);

        SubTask subTask2 = new SubTask("Subtask", "test subtask", Status.NEW);
        taskManager.createTask(subTask2);

        taskManager.fillEpic(epic, subTask);
        taskManager.fillEpic(epic, subTask2);

        List<SubTask> currentSubtaskList = new ArrayList<>(List.of(subTask, subTask2));

        assertNotNull(epic.getTaskList(), "Список подзадач не найден");
        assertEquals(currentSubtaskList, epic.getTaskList(), "Содержимое списков не совпадает");

    }

    @Test
    public void updateEpicWhenChangingSubtaskStatus() {
        Epic epic = new Epic("Epic", "test epic", Status.NEW);
        taskManager.createTask(epic);

        SubTask subTask = new SubTask("Subtask", "test subtask", Status.NEW);
        taskManager.createTask(subTask);
        SubTask subTask2 = new SubTask("Subtask 2", "test subtask 2", Status.NEW);
        taskManager.createTask(subTask2);

        taskManager.fillEpic(epic, subTask);
        taskManager.fillEpic(epic, subTask2);

        assertEquals(Status.NEW, epic.getStatus(), "Статусы не совпадают");

        subTask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(subTask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статусы не совпадают");

        subTask = new SubTask("Subtask", "test subtask", subTask.getId(), Status.DONE);
        taskManager.updateTask(subTask);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статусы не совпадают");

        subTask2 = new SubTask("Subtask 2", "test subtask 2", subTask2.getId(), Status.DONE);
        taskManager.updateTask(subTask2);

        assertEquals(Status.DONE, epic.getStatus(), "Статусы не совпадают");
    }

    @Test
    public void UpdateEpicWhenDeleteSubtaskById() {
        Epic epic = new Epic("Epic", "test epic", Status.NEW);
        taskManager.createTask(epic);

        SubTask subTask = new SubTask("Subtask", "test subtask", Status.NEW);
        taskManager.createTask(subTask);
        SubTask subTask2 = new SubTask("Subtask 2", "test subtask 2", Status.IN_PROGRESS);
        taskManager.createTask(subTask2);
        SubTask subTask3 = new SubTask("Subtask 3", "test subtask 3", Status.DONE);
        taskManager.createTask(subTask3);

        taskManager.fillEpic(epic, subTask);
        taskManager.fillEpic(epic, subTask2);
        taskManager.fillEpic(epic, subTask3);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статусы не совпадают");

        taskManager.deleteSubTaskById(subTask.getId());
        taskManager.deleteSubTaskById(subTask2.getId());

        assertEquals(Status.DONE, epic.getStatus(), "Статусы не совпадают");
    }

    @Test
    public void deleteAllTasksTest() {
        Task task = new Task("Task", "test task", Status.NEW);
        Task task2 = new Task("Task2", "test task2", Status.NEW);
        Task task3 = new Task("Task3", "test task3", Status.NEW);
        taskManager.createTask(task);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        assertEquals(3, taskManager.getTasks().size(), "Размеры коллекций не совпадают");

        taskManager.deleteAllTasks();

        assertEquals(0, taskManager.getTasks().size(), "Коллекция не пустая");
    }

    @Test
    public void deleteAllSubtaskTest() {
        Epic epic = new Epic("Epic", "test epic", Status.NEW);
        taskManager.createTask(epic);

        SubTask subTask = new SubTask("Subtask", "test subtask", Status.NEW);
        taskManager.createTask(subTask);
        SubTask subTask2 = new SubTask("Subtask 2", "test subtask 2", Status.IN_PROGRESS);
        taskManager.createTask(subTask2);
        SubTask subTask3 = new SubTask("Subtask 3", "test subtask 3", Status.DONE);
        taskManager.createTask(subTask3);

        taskManager.fillEpic(epic, subTask);
        taskManager.fillEpic(epic, subTask2);
        taskManager.fillEpic(epic, subTask3);

        assertEquals(3, taskManager.getSubTasks().size(), "Размеры коллекций не совпадают");
        assertEquals(3, epic.getTaskList().size(), "Размеры коллекций не совпадают");

        taskManager.deleteAllSubtasks();

        assertEquals(0, taskManager.getSubTasks().size(), "Коллекция не пустая");
        assertEquals(0, epic.getTaskList().size(), "Список подзадач эпика не пустой");
    }

    @Test
    public void deleteAllEpicTest() {
        Epic epic = new Epic("Epic", "test epic", Status.NEW);
        taskManager.createTask(epic);
        Epic epic2 = new Epic("Epic 2", "test epic 2", Status.NEW);
        taskManager.createTask(epic2);

        SubTask subTask = new SubTask("Subtask", "test subtask", Status.NEW);
        taskManager.createTask(subTask);
        SubTask subTask2 = new SubTask("Subtask 2", "test subtask 2", Status.IN_PROGRESS);
        taskManager.createTask(subTask2);
        SubTask subTask3 = new SubTask("Subtask 3", "test subtask 3", Status.DONE);
        taskManager.createTask(subTask3);

        taskManager.fillEpic(epic, subTask);
        taskManager.fillEpic(epic, subTask2);
        taskManager.fillEpic(epic2, subTask3);

        assertEquals(3, taskManager.getSubTasks().size(), "Размеры коллекций не совпадают");
        assertEquals(2, taskManager.getEpics().size(), "Размеры коллекций не совпадают");

        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getSubTasks().size(), "Коллекция не пустая");
        assertEquals(0, taskManager.getEpics().size(), "Коллекция не пустая");
    }

    @Test
    public void deleteTaskByIdTest() {
        Task task = new Task("Task", "test task", Status.NEW);
        Task task2 = new Task("Task2", "test task2", Status.NEW);
        Task task3 = new Task("Task3", "test task3", Status.NEW);
        taskManager.createTask(task);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        HashMap<Integer, Task> current = new HashMap<>(Map.of(1, task, 2, task2, 3, task3));

        taskManager.deleteTaskById(-1000);

        assertEquals(current, taskManager.getTasks(), "Содержимое коллекций не совпадает");

        taskManager.deleteTaskById(task.getId());
        current.remove(task.getId());

        assertEquals(current, taskManager.getTasks(), "Содержимое коллекций не совпадает");
    }

    @Test
    public void deleteEpicByIdTest() {
        Epic epic = new Epic("Epic", "test epic", Status.NEW);
        taskManager.createTask(epic);
        Epic epic2 = new Epic("Epic 2", "test epic 2", Status.NEW);
        taskManager.createTask(epic2);

        SubTask subTask = new SubTask("Subtask", "test subtask", Status.NEW);
        taskManager.createTask(subTask);
        SubTask subTask2 = new SubTask("Subtask 2", "test subtask 2", Status.IN_PROGRESS);
        taskManager.createTask(subTask2);
        SubTask subTask3 = new SubTask("Subtask 3", "test subtask 3", Status.DONE);
        taskManager.createTask(subTask3);

        HashMap<Integer, SubTask> currentSubtasks = new HashMap<>(Map.of(subTask.getId(), subTask, subTask2.getId(), subTask2, subTask3.getId(), subTask3));
        HashMap<Integer, Epic> currentEpics = new HashMap<>(Map.of(epic.getId(), epic, epic2.getId(), epic2));

        taskManager.fillEpic(epic, subTask);
        taskManager.fillEpic(epic, subTask2);
        taskManager.fillEpic(epic2, subTask3);

        taskManager.deleteTaskById(49320419);

        assertEquals(currentSubtasks, taskManager.getSubTasks(), "Содержимое коллекций не совпадает");
        assertEquals(currentEpics, taskManager.getEpics(), "Содержимое коллекций не совпадает");

        taskManager.deleteEpicById(epic.getId());
        currentEpics.remove(epic.getId());
        currentSubtasks.remove(subTask.getId());
        currentSubtasks.remove(subTask2.getId());

        assertEquals(currentSubtasks, taskManager.getSubTasks(), "Содержимое коллекций не совпадает");
        assertEquals(currentEpics, taskManager.getEpics(), "Содержимое коллекций не совпадает");
    }

    @Test
    public void getEndTimeTest() {
        Task task = new Task("Task", "test task", Status.NEW,
                LocalDateTime.of(2022, 5, 3, 10, 0), Duration.ofMinutes(10));
        taskManager.createTask(task);

        LocalDateTime endTime = LocalDateTime.of(2022, 5, 3, 10, 0).plusMinutes(10);

        assertNotNull(task.getEndTime(), "Время завершения задачи не найдено");
        assertEquals(endTime, task.getEndTime(), "Время завершения задачи не совпадает");
    }

    @Test
    public void getEpicsStartTimeAndEndTimeTest() {
        Epic epic = new Epic("Epic", "test epic", Status.NEW);
        taskManager.createTask(epic);

        SubTask subTask = new SubTask("Subtask", "test subtask", Status.NEW,
                LocalDateTime.of(2022, 5, 3, 15, 15), Duration.ofMinutes(30));
        taskManager.createTask(subTask);

        SubTask subTask2 = new SubTask("Subtask 2", "test subtask 2",
                Status.IN_PROGRESS, LocalDateTime.of(2022, 5, 3, 5, 10), Duration.ofMinutes(145));
        taskManager.createTask(subTask2);

        SubTask subTask3 = new SubTask("Subtask 3", "test subtask 3", Status.DONE,
                LocalDateTime.of(2022, 5, 3, 20, 15), Duration.ofMinutes(15));
        taskManager.createTask(subTask3);

        taskManager.fillEpic(epic, subTask);
        taskManager.fillEpic(epic, subTask2);
        taskManager.fillEpic(epic, subTask3);

        LocalDateTime endTime = LocalDateTime.of(2022, 5, 3, 20, 15).plusMinutes(15);
        LocalDateTime startTime = LocalDateTime.of(2022, 5, 3, 5, 10);

        assertNotNull(epic.getEndTime(), "Время завершения задачи не найдено");
        assertNotNull(epic.getStartTime(), "Время начала задачи не найдено");

        assertEquals(endTime, epic.getEndTime(), "Время завершения задачи не совпадает");
        assertEquals(startTime, epic.getStartTime(), "Время начала задачи не совпадает");
    }

    @Test
    public void isCrossingCheckPrioritizedTasks() {
        Task task = new Task("Task", "test task", Status.NEW,
                LocalDateTime.of(2022, 5, 3, 10, 0), Duration.ofMinutes(10));
        taskManager.createTask(task);

        Task task2 = new Task("Task-2", "test task-2", Status.NEW,
                LocalDateTime.of(2022, 5, 3, 9, 40), Duration.ofMinutes(25));
        taskManager.createTask(task2);

        Epic epic = new Epic("Epic", "test epic", Status.NEW);
        taskManager.createTask(epic);

        SubTask subTask = new SubTask("Subtask", "test subtask", Status.NEW,
                LocalDateTime.of(2022, 5, 3, 15, 15), Duration.ofMinutes(30));
        taskManager.createTask(subTask);

        SubTask subTask2 = new SubTask("Subtask 2", "test subtask 2",
                Status.IN_PROGRESS, LocalDateTime.of(2022, 5, 3, 17, 10), Duration.ofMinutes(160));
        taskManager.createTask(subTask2);

        SubTask subTask3 = new SubTask("Subtask 3", "test subtask 3", Status.DONE,
                LocalDateTime.of(2022, 5, 3, 18, 15), Duration.ofMinutes(5));
        taskManager.createTask(subTask3);

        taskManager.fillEpic(epic, subTask);
        taskManager.fillEpic(epic, subTask2);
        taskManager.fillEpic(epic, subTask3);

        assertNull(taskManager.getTaskById(task2.getId()).getStartTime(), "Неверно установлено время задачи");
        assertNull(taskManager.getTaskById(subTask3.getId()).getStartTime(), "Неверно установлено время задачи");

        assertEquals(task, taskManager.getPrioritizedTasks().first(), "Задача не совпадает");
        assertEquals(subTask3, taskManager.getPrioritizedTasks().last(), "Задача не совпадает");
    }
}