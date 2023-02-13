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
        /*Task task = new Task("Тестовая задача", "Тестовое описание", Status.NEW,
                LocalDateTime.of(2025, 5, 5, 12, 30), Duration.ofMinutes(15));
        taskManager.createTask(task);
        System.out.println(task.getStartTime());
        System.out.println(task.getDuration());
        System.out.println(task.getEndTime());*/


        Epic epic = new Epic("Epic", "Test epic", Status.NEW);
        taskManager.createTask(epic);

        SubTask subTask1 = new SubTask("1", "1", Status.NEW,
                LocalDateTime.of(2025, 5, 5, 13, 30), Duration.ofMinutes(25));
        taskManager.createTask(subTask1);

        Task task = new Task("Кот", "Купить корм для кота", Status.NEW,
                LocalDateTime.of(2025, 5, 5, 14, 10), Duration.ofMinutes(1));
        taskManager.createTask(task);

        SubTask subTask2 = new SubTask("2", "2", Status.NEW,
                LocalDateTime.of(2025, 5, 5, 13, 00), Duration.ofMinutes(10));
        taskManager.createTask(subTask2);

        SubTask subTask3 = new SubTask("3", "3", Status.NEW,
                LocalDateTime.of(2025, 5, 5, 18, 30), Duration.ofMinutes(90));
        taskManager.createTask(subTask3);

        SubTask subTask4 = new SubTask("4", "4", Status.IN_PROGRESS);
        taskManager.createTask(subTask4);

        /*taskManager.fillEpic(epic, subTask1);
        taskManager.fillEpic(epic, subTask2);
        taskManager.fillEpic(epic, subTask3);
        taskManager.fillEpic(epic, subTask4);*/

        System.out.println(taskManager.getPrioritizedTasks());




    }
}
