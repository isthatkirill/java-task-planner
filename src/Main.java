import tracker.controllers.InMemoryTaskManager;
import tracker.interfaces.TaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.SubTask;
import tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic", "Test epic", Status.NEW);
        taskManager.createTask(epic);

        Epic epic2 = new Epic("Epic2", "Test epic2", Status.NEW);
        taskManager.createTask(epic);

        SubTask subTask1 = new SubTask("1", "1", Status.NEW,
                LocalDateTime.of(2025, 5, 5, 13, 30), Duration.ofMinutes(25));
        taskManager.createTask(subTask1);

        Task task = new Task("Кот", "Купить корм для кота", Status.NEW,
                LocalDateTime.of(2025, 5, 6, 13, 10), Duration.ofMinutes(100));
        taskManager.createTask(task);

        Task task2 = new Task("Кот-rjn", "??????", Status.NEW,
                LocalDateTime.of(2025, 5, 7, 13, 10), Duration.ofMinutes(100));
        taskManager.createTask(task2);

        SubTask subTask2 = new SubTask("ab ab ab", "ab ab ab", Status.NEW,
                LocalDateTime.of(2025, 5, 5, 19, 10), Duration.ofMinutes(10));
        taskManager.createTask(subTask2);

        SubTask subTask3 = new SubTask("3", "3", Status.NEW,
                LocalDateTime.of(2025, 5, 5, 16, 30), Duration.ofMinutes(90));
        taskManager.createTask(subTask3);

        SubTask subTask4 = new SubTask("4", "4", Status.IN_PROGRESS);
        taskManager.createTask(subTask4);

        taskManager.fillEpic(epic, subTask1);
        taskManager.fillEpic(epic, subTask2);
        taskManager.fillEpic(epic, subTask3);
        taskManager.fillEpic(epic, subTask4);

        System.out.println(taskManager);

        subTask2 = new SubTask("2", "2", subTask2.getId(), Status.NEW,
                LocalDateTime.of(2025, 6, 5, 10, 40), Duration.ofMinutes(30));
        taskManager.updateTask(subTask2);

        taskManager.getTaskById(subTask2.getId());
        taskManager.getTaskById(subTask3.getId());
        taskManager.getTaskById(subTask1.getId());
        taskManager.getTaskById(epic.getId());
        taskManager.getTaskById(subTask3.getId());
        taskManager.getTaskById(task.getId());

        System.out.println(taskManager);
        System.out.println(taskManager.getHistoryManager().getHistory());

        taskManager.deleteAllEpics();

        System.out.println(taskManager);
        System.out.println(taskManager.getHistoryManager().getHistory());

    }
}
