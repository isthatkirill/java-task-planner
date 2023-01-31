package tracker.controllers;

import tracker.customExceptions.ManagerSaveException;
import tracker.interfaces.HistoryManager;
import tracker.interfaces.TaskManager;
import tracker.model.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private final String path;

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

    @Override
    public Task getTaskById(int id) {
        var task = super.getTaskById(id);
        save();
        return task;
    }


    public void save() {
        try (PrintWriter printWriter = new PrintWriter(path, "Cp1251")) { //корректное отображение кириллицы
            printWriter.write("id;type;name;status;description;epic\n");            // в Excel
            for (Task task : super.getTasks().values()) {
                printWriter.write(toString(task));
            }
            for (Epic epic : super.getEpics().values()) {
                printWriter.write(toString(epic));
            }
            for (SubTask subTask : super.getSubTasks().values()) {
                printWriter.write(toString(subTask));
            }
            printWriter.write("\n");
            printWriter.write(historyToString(this.getHistoryManager()));
            printWriter.close();

        } catch (IOException e) {
            throw new ManagerSaveException("Error when working with the file");
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file, Charset.forName("Cp1251")))) {
            br.readLine();
            int newCurrentIdCounter = 0;

            while (br.ready()) {
                newCurrentIdCounter++;
                String line = br.readLine();
                if (line.isBlank()) {
                    List<Integer> viewedTasksId = historyFromString(br.readLine());
                    HistoryManager historyManager = fileManager.getHistoryManager();
                    for (Integer i : viewedTasksId) {
                        if (fileManager.tasks.containsKey(i)) {
                            historyManager.add(fileManager.tasks.get(i));
                        } else if (fileManager.subTasks.containsKey(i)) {
                            historyManager.add(fileManager.subTasks.get(i));
                        } else if (fileManager.epics.containsKey(i)) {
                            historyManager.add(fileManager.epics.get(i));
                        }
                    }
                    break;
                }

                String[] splitLine = line.split(";");
                if (splitLine[1].equals(TaskTypes.Task.toString())) {
                    Task task = new Task(splitLine[2], splitLine[4], Integer.parseInt(splitLine[0]),
                            Status.valueOf(splitLine[3]));
                    fileManager.tasks.put(Integer.parseInt(splitLine[0]), task);
                } else if (splitLine[1].equals(TaskTypes.Epic.toString())) {
                    Epic epic = new Epic(splitLine[2], Integer.parseInt(splitLine[0]), splitLine[4],
                            Status.valueOf(splitLine[3]));
                    fileManager.epics.put(Integer.parseInt(splitLine[0]), epic);
                } else if (splitLine[1].equals(TaskTypes.SubTask.toString())) {
                    SubTask subTask = new SubTask(splitLine[2], splitLine[4], Integer.parseInt(splitLine[0]),
                            Status.valueOf(splitLine[3]));
                    subTask.setEpicsId(Integer.parseInt(splitLine[5]));
                    fileManager.subTasks.put(Integer.parseInt(splitLine[0]), subTask);

                    Epic epic = fileManager.epics.get(subTask.getEpicsId());
                    ArrayList<SubTask> temp = epic.getTaskList();
                    temp.add(subTask);
                }
            }
            fileManager.setCurrentId(newCurrentIdCounter);
        } catch (IOException e) {
            throw new ManagerSaveException("Error when working with the file");
        }
        return fileManager;
    }

    public static List<Integer> historyFromString(String value) {
        String[] splittedLine = value.split(";");
        List<Integer> viewedTasksId = new ArrayList<>();
        for (String s : splittedLine) {
            viewedTasksId.add(Integer.parseInt(s));
        }
        return viewedTasksId;
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder idOfViewedTasks = new StringBuilder();
        for (Task task : manager.getHistory()) {
            idOfViewedTasks.append(task.getId()).append(";");
        }
        return idOfViewedTasks.toString();
    }

    public String toString(Task task) {
        if (task.getClass() == SubTask.class) {
            return task.getId() + ";" + task.getClass().getSimpleName() + ";" + task.getTitle() + ";" +
                    task.getStatus() + ";" + task.getDescription() + ";" + ((SubTask) task).getEpicsId() + "\n";
        } else {
            return task.getId() + ";" + task.getClass().getSimpleName() + ";" + task.getTitle() + ";" +
                    task.getStatus() + ";" + task.getDescription() + "\n";
        }
    }

    public void setCurrentId(int id) {
        this.current_id = id;
    }

    public static void main(String[] args) {

        //-----------------------------------запись в файл--------------------------------//

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
        fileManager.updateTask(subtask);

        fileManager.getTaskById(task.getId());
        fileManager.getTaskById(subTask2.getId());
        fileManager.getTaskById(subtask.getId());
        fileManager.getTaskById(epic.getId());
        fileManager.getTaskById(subTask2.getId());

        System.out.println("-------------------------filemanager (записанный в файл)------------------------");
        System.out.println(fileManager);
        System.out.println(fileManager.getHistoryManager().getHistory());
        System.out.println();

        //---------------------------чтение из уже существующего файла------------------------------//

        FileBackedTasksManager fileManagerBacked = loadFromFile(new File("resources/data.csv"));
        System.out.println("------------------------fileManagerBacked (восстановленный из файла)----------------");
        System.out.println(fileManagerBacked);
        System.out.println(fileManagerBacked.getHistoryManager().getHistory());

    }


}
