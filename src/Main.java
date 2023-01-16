import tracker.interfaces.HistoryManager;
import tracker.interfaces.TaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.util.Managers;

public class Main {

    public static void main(String[] args) {
        test_();
    }

    static public void test_() {

        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = taskManager.getHistoryManager();

        Task task = new Task("Оплатить обучение", "Оплатить обучение до 02.02.", Status.NEW);
        taskManager.createTask(task);

        Epic epic = new Epic("Уборка", "Генеральная уборка в эту среду", Status.NEW);
        taskManager.createTask(epic);

        SubTask subtask = new SubTask("Мытье полов", "Помыть полы во всех комнатах", Status.NEW);
        taskManager.createTask(subtask);
        taskManager.fillEpic(epic, subtask);

        //------------------------проверка правильности вывода просмотренных задач--------------------//


        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(1);

        System.out.println(historyManager.getHistory());

        //--------------------проверка правильности удаления старых задач из списка просмотренных--------------//


        taskManager.deleteEpicById(2);
        System.out.println(historyManager.getHistory());



    }
}
