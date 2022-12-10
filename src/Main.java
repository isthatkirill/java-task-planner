public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

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

        System.out.println(taskManager.getEpics()); //IN_PROGRESS

        subtask = new SubTask("Посуда", "Посуда вымыта", subtask.getId(), "DONE");
        taskManager.updateTask(subtask);

        System.out.println(taskManager.getEpics()); //DONE

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

        subtask_ = new SubTask("Цветы", "Цветы куплены, едем к подруге...", subtask_.getId(),  "IN_PROGRESS");
        taskManager.updateTask(subtask_);

        taskManager.deleteAllSubtasks(epic_);

        System.out.println(taskManager.getEpics());

    }
}
