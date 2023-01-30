package tracker.controllers;

import tracker.customExceptions.ManagerSaveException;
import tracker.interfaces.TaskManager;
import tracker.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private String path;

    FileBackedTasksManager(String path) {
        this.path = path;
    }


    @Override
    public void createTask(Task o) {
        super.createTask(o);
        save();
    }

    @Override
    public void updateTask(Task o) {
        super.updateTask(o);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    public void save() {
        try (PrintWriter printWriter = new PrintWriter(path, "Cp1251")) {
            printWriter.write("id;type;name;status;description;epic\n");
            for (Task task : super.getTasks().values()) {
                printWriter.write(toString(task));
            }
            for (Epic epic : super.getEpics().values()) {
                printWriter.write(toString(epic));
            }
            for (SubTask subTask : super.getSubTasks().values()) {
                printWriter.write(toString(subTask));
            }

            printWriter.write(" ");
            printWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Error when working with the file");
        }

    }

    public String toString(Task task) {
        if (task.getClass() == SubTask.class) {
            return task.getId() + ";" + task.getClass().getSimpleName() + ";" + task.getTitle() + ";" +
                    task.getStatus() + ";" + task.getDescription() + ";" +  ((SubTask) task).getEpicsId() + "\n" ;
        } else {
            return task.getId() + ";" + task.getClass().getSimpleName() + ";" + task.getTitle() + ";" +
                    task.getStatus() + ";" + task.getDescription() + "\n" ;
        }
    }



    public static void main(String[] args) {
        FileBackedTasksManager fileManager = new FileBackedTasksManager("resources/data.csv");
        Task task = new Task("Кот", "Купить корм для кота", Status.NEW);
        fileManager.createTask(task);


        Epic epic = new Epic("Уборка", "Генеральная уборка в эту среду", Status.NEW);
        fileManager.createTask(epic);

        SubTask subtask = new SubTask("Мытье полов", "Помыть полы во всех комнатах", Status.NEW);
        fileManager.fillEpic(epic, subtask);
        fileManager.createTask(subtask);

        SubTask subTask2 = new SubTask("Пылесос", "Пропылесосить кладовку", Status.NEW);
        fileManager.fillEpic(epic, subTask2);
        fileManager.createTask(subTask2);

        task = new Task("Кот", "Корм куплен", task.getId(), Status.DONE);
        fileManager.updateTask(task);

        subtask = new SubTask("Мытье полов", "Полы вымыты", subtask.getId(), Status.DONE);

        fileManager.deleteAllSubtasks();

        System.out.println(fileManager.getTasks());
        System.out.println(fileManager.getEpics());
        System.out.println(fileManager.getSubTasks());
    }



}
