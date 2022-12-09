public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        /*Task task = new Task("Новая тестовая задача", "Проверяем работоспособность", "NEW");
        taskManager.createTask(task);
        SubTask subtask = new SubTask("Помыть посуду", "Не хочу", "NEW");
        taskManager.createTask(subtask);


        System.out.println(taskManager);

        subtask = new SubTask("dadad", "dsadadsa", subtask.getId(), "dsdaasd");
        taskManager.updateTask(subtask);



        System.out.println(taskManager);

        taskManager.deleteTaskById(1);
        System.out.println(taskManager);
        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getTaskById(2));*/

        /*SubTask subtask = new SubTask("Новая тестовая задача", "Проверяем работоспособность", "NEW");
        taskManager.createSubTask(subtask);
        System.out.println(taskManager);*/

        Epic epic = new Epic("Уборка", "Генеральная уборка в воскресенье", "NEW");
        taskManager.createTask(epic);

        SubTask subtask = new SubTask("Посуда", "Нужно помыть посуду", "NEW");
        taskManager.createTask(subtask);
        SubTask subtask_1 = new SubTask("Пыль", "Нужно вытереть пыль", "NEW");
        taskManager.createTask(subtask_1);

        taskManager.fillEpic(epic, subtask);
        taskManager.fillEpic(epic, subtask_1);

        System.out.println(taskManager.getEpics());


        subtask_1 = new SubTask("Пыль", "Почти закончили с пылью", subtask_1.getId(), "IN_PROGRESS");
        taskManager.updateTask(subtask_1);
        SubTask subtask_3 = new SubTask("Уборка ванной", "Убрать санузел (туалет + ванная)", "NEW");
        taskManager.fillEpic(epic, subtask_3);

        
        System.out.println(taskManager.getEpics());
        taskManager.deleteSubTaskById(2);
        System.out.println(taskManager.getEpics());
        taskManager.deleteEpicById(1);
        System.out.println(taskManager.getEpics());



    }
}
