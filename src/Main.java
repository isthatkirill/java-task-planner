import tracker.model.Epic;
import tracker.model.Task;
import tracker.model.SubTask;
import tracker.controllers.InMemoryTaskManager;
import tracker.interfaces.TaskManager;
public class Main {

    public static void main(String[] args) {
        //test_();
        test();

    }
    static public void test() {
        TaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Уборка", "Генеральная уборка в воскресенье", "NEW");
        taskManager.createTask(epic);

        SubTask subtask = new SubTask("Посуда", "Нужно помыть посуду", "NEW");
        taskManager.createTask(subtask);
        SubTask subtask_1 = new SubTask("Полы", "Помыть полы во всех комнатах", "NEW");

        taskManager.createTask(subtask_1);
        taskManager.fillEpic(epic, subtask);
        taskManager.fillEpic(epic, subtask_1);

        System.out.println(taskManager.getEpics()); //NEW

        subtask_1 = new SubTask("Полы", "Полы вымыты", subtask_1.getId(), "DONE");
        taskManager.updateTask(subtask_1);

        epic.setStatus("CHECKER"); // СТАТУС НЕ ОБНОВЛЯЕТСЯ

        System.out.println(taskManager.getEpics()); //IN_PROGRESS

        subtask = new SubTask("Посуда", "Начинаем мыть посуду", subtask.getId(), "IN_PROGRESS");
        taskManager.updateTask(subtask);
        System.out.println(taskManager.getEpics());

        taskManager.deleteSubTaskById(subtask.getId());

        System.out.println(taskManager.getEpics()); //DONE

        System.out.println();

        /*--------------------------------------------------------------------------------------------------------*/

        Epic epic_ = new Epic("День рождения подруги", "12.12.2022 в 17:00", "NEW");
        taskManager.createTask(epic_);

        SubTask subtask_ = new SubTask("Цветы", "Купить пионы для подруги",  "NEW");
        taskManager.createTask(subtask_);
        taskManager.fillEpic(epic_, subtask_);

        System.out.println(taskManager.getEpics()); //NEW

        subtask_ = new SubTask("Цветы", "Цветы куплены, едем к подруге...", subtask_.getId(),
                "IN_PROGRESS");

        taskManager.updateTask(subtask_);
        System.out.println(taskManager.getEpics());
        System.out.println();

        taskManager.deleteEpicById(1); //удаление эпика влечет за собой удаление всех подзадач этого эпика из списка
        // в tracker.controllers.TaskManager
        System.out.println(taskManager.getSubTasks());
        System.out.println(taskManager.getEpics());

        System.out.println();

        /*--------------------------------------------------------------------------------------------------------*/

        taskManager.deleteAllSubtasks(); //удаление всех подзадач
        System.out.println(taskManager.getEpics()); // у всех эпиков больше нет подзадач
    }

    static public void test_() {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("1", "1", "1");
        taskManager.createTask(task);

        Task task1 = new Task("2", "2", "2");
        taskManager.createTask(task1);

        Task task2 = new Task("3", "3", "3");
        taskManager.createTask(task2);

        Task task3 = new Task("4", "4", "4");
        taskManager.createTask(task3);

        Task task4 = new Task("5", "5", "5");
        taskManager.createTask(task4);

        Task task5 = new Task("6", "6", "6");
        taskManager.createTask(task5);

        Task task6 = new Task("7", "7", "7");
        taskManager.createTask(task6);

        Task task7 = new Task("8", "8", "1");
        taskManager.createTask(task7);

        Task task8 = new Task("9", "9", "9");
        taskManager.createTask(task8);

        Task task9 = new Task("10", "10", "10");
        taskManager.createTask(task9);

        Task task10 = new Task("11", "11", "11");
        taskManager.createTask(task10);

        Task task11 = new Task("12", "12", "12");
        taskManager.createTask(task11);

        Task task12 = new Task("13", "13", "13");
        taskManager.createTask(task12);

        for (int i = 1; i < 15; i++) {
            System.out.println(taskManager.getTaskById(i));
        }
        System.out.println(taskManager.getHistory());



    }
}
