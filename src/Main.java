import tracker.interfaces.HistoryManager;
import tracker.model.*;
import tracker.controllers.InMemoryTaskManager;
import tracker.interfaces.TaskManager;
import tracker.util.Managers;

public class Main {

    public static void main(String[] args) {
        test_();
    }


    static public void test_() {

        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task = new Task("Оплатить обучение", "Оплатить обучение до 02.02.", Status.NEW);
        taskManager.createTask(task);

        Task epic = new Epic("Уборка", "Генеральная уборка в эту среду", Status.NEW);
        taskManager.createTask(epic);

        Task subtask = new SubTask("Мытье полов", "Помыть полы во всех комнатах", Status.NEW);
        taskManager.createTask(subtask);

        //------------------------проверка правильности вывода просмотренных задач--------------------//

        for (int i = 1; i < 5; i++) {
            taskManager.getTaskById(i);
        }
        System.out.println(historyManager.getHistory());

        //--------------------проверка правильности удаления старых задач из списка просмотренных--------------//

        for (int i = 0; i < 10; i++) {
            taskManager.getTaskById(1);
        }
        taskManager.getTaskById(2);
        System.out.println(historyManager.getHistory());

    }
}
