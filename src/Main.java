import tracker.interfaces.HistoryManager;
import tracker.interfaces.TaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.util.Managers;

public class Main {

    public static void main(String[] args) {
        test();
    }

    public static void test() {

        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = taskManager.getHistoryManager();

        //-----------------------------------задача 1---------------------------------------------------//

        Task task = new Task("Оплатить обучение", "Оплатить обучение до 02.02.", Status.NEW);
        taskManager.createTask(task);

        //----------------------------------задача 2----------------------------------------------------//

        Task task2 = new Task("Заправить машину", "Полный бак", Status.NEW);
        taskManager.createTask(task2);

        //-----------------------------------эпик с тремя подзадачами-------------------------------//

        Epic epic = new Epic("Уборка", "Генеральная уборка в эту среду", Status.NEW);
        taskManager.createTask(epic);

        SubTask subtask = new SubTask("Мытье полов", "Помыть полы во всех комнатах", Status.NEW);
        taskManager.createTask(subtask);
        SubTask subTask2 = new SubTask("Пылесос", "Пропылесосить кладовку", Status.NEW);
        taskManager.createTask(subTask2);
        SubTask subTask3 = new SubTask("Мебель", "Переставить мебель в гостинной", Status.NEW);
        taskManager.createTask(subTask3);

        taskManager.fillEpic(epic, subtask);
        taskManager.fillEpic(epic, subTask2);
        taskManager.fillEpic(epic, subTask3);

        //---------------------------------пустой эпик------------------------------------------------//

        Epic epic2 = new Epic("День рождения", "День рождения Насти", Status.NEW);
        taskManager.createTask(epic2);

        //------------------------добавление задач в список просмотренных--------------------//

        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(epic.getId());
        taskManager.getTaskById(subtask.getId());      //  добавление в список просмотренных субтасков
        taskManager.getTaskById(subTask2.getId());     //  одного из эпиков
        taskManager.getTaskById(epic2.getId());
        System.out.println(historyManager.getHistory() + "\n");

        //------------------------проверка на сохранение только последнего просморта----------------------//

        taskManager.getTaskById(task.getId());
        System.out.println(historyManager.getHistory() + "\n");
        taskManager.getTaskById(epic2.getId());
        System.out.println(historyManager.getHistory() + "\n");

        //--------------------проверка правильности удаления тасков и эпиков---------------------------------//

        taskManager.deleteTaskById(task.getId());
        System.out.println(historyManager.getHistory() + "\n");

        taskManager.deleteTaskById(epic.getId());    // из истории просмотров удалился как эпик, так и два
        System.out.println(historyManager.getHistory() + "\n"); // просмотренных субтаска


    }
}
