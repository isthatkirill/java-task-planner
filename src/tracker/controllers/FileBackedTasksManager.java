package tracker.controllers;

import tracker.exceptions.ManagerSaveException;
import tracker.interfaces.HistoryManager;
import tracker.interfaces.TaskManager;
import tracker.model.*;

import java.io.*;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private final String path;

    public FileBackedTasksManager(String path) {
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

    @Override
    public void fillEpic(Epic epic, SubTask subTask) {
        super.fillEpic(epic, subTask);
        save();
    }


    public void save() {
        try (PrintWriter printWriter = new PrintWriter(path, "Cp1251")) { //корректное отображение кириллицы
            printWriter.write("id;type;name;status;description;epic;startTime;duration;endTime\n");            // в Excel
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

                newCurrentIdCounter++;
                String[] splitLine = line.split(";");
                if (splitLine[1].equals(TaskTypes.Task.toString())) {
                    Task task;
                    if (splitLine.length == 9) {
                        task = new Task(splitLine[2], splitLine[4], Integer.parseInt(splitLine[0]),
                                Status.valueOf(splitLine[3]), LocalDateTime.parse(splitLine[6],
                                fileManager.dateTimeFormatter), Duration.parse(splitLine[7]));
                    } else {
                        task = new Task(splitLine[2], splitLine[4], Integer.parseInt(splitLine[0]),
                                Status.valueOf(splitLine[3]));
                    }
                    fileManager.taskByTime.add(task);
                    fileManager.tasks.put(Integer.parseInt(splitLine[0]), task);

                } else if (splitLine[1].equals(TaskTypes.Epic.toString())) {
                    Epic epic = new Epic(splitLine[2], Integer.parseInt(splitLine[0]), splitLine[4],
                            Status.valueOf(splitLine[3]));
                    fileManager.epics.put(Integer.parseInt(splitLine[0]), epic);

                } else if (splitLine[1].equals(TaskTypes.SubTask.toString())) {
                    SubTask subTask;
                    if (splitLine.length == 9) {
                        subTask = new SubTask(splitLine[2], splitLine[4], Integer.parseInt(splitLine[0]),
                                Status.valueOf(splitLine[3]), LocalDateTime.parse(splitLine[6],
                                fileManager.dateTimeFormatter), Duration.parse(splitLine[7]));
                    } else {
                        subTask = new SubTask(splitLine[2], splitLine[4], Integer.parseInt(splitLine[0]),
                                Status.valueOf(splitLine[3]));
                    }

                    subTask.setEpicsId(Integer.parseInt(splitLine[5]));
                    fileManager.taskByTime.add(subTask);
                    fileManager.subTasks.put(Integer.parseInt(splitLine[0]), subTask);

                    Epic epic = fileManager.epics.get(subTask.getEpicsId());
                    ArrayList<SubTask> temp = epic.getTaskList();
                    temp.add(subTask);
                    epic.setTaskList(temp);
                }
            }
            fileManager.setCurrentId(newCurrentIdCounter);
        } catch (IOException e) {
            throw new ManagerSaveException("Error when working with the file");
        }
        return fileManager;
    }

    public static List<Integer> historyFromString(String value) {
        if (value != null) {
            String[] splittedLine = value.split(";");
            List<Integer> viewedTasksId = new ArrayList<>();
            for (String s : splittedLine) {
                viewedTasksId.add(Integer.parseInt(s));
            }
            return viewedTasksId;
        } else {
            return Collections.emptyList();
        }
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder idOfViewedTasks = new StringBuilder();
        for (Task task : manager.getHistory()) {
            idOfViewedTasks.append(task.getId()).append(";");
        }
        return idOfViewedTasks.toString();
    }

    public String toString(Task task) {
        String data = task.getId() + ";" + task.getClass().getSimpleName() + ";" + task.getTitle() + ";" +
                task.getStatus() + ";" + task.getDescription();

        if (task.getClass() == SubTask.class) {
            data = data + ";" + ((SubTask) task).getEpicsId() + ";";
        } else {
            data = data + ";" + ";";
        }

        if (task.getStartTime() != null && task.getEndTime() != null) {
            data = data + task.getStartTime().format(dateTimeFormatter) + ";" +
                    task.getDuration() + ";" + task.getEndTime().format(dateTimeFormatter) + "\n";
        } else {
            data = data + "\n";
        }

        return data;
    }

    public void setCurrentId(int id) {
        this.current_id = id;
    }

    public static void main(String[] args) {

        //-----------------------------------запись в файл--------------------------------//

        FileBackedTasksManager fileManager = new FileBackedTasksManager("resources/data.csv");
        Task task = new Task("Кот", "Купить корм для кота", Status.NEW,
                LocalDateTime.of(2025, 5, 5, 16, 10), Duration.ofMinutes(10));
        fileManager.createTask(task);
        fileManager.getTaskById(task.getId());

        Epic epic = new Epic("Уборка", "Генеральная уборка в эту среду", Status.NEW);
        fileManager.createTask(epic);

        SubTask subTask2 = new SubTask("Test", "Test subtask", Status.NEW,
                LocalDateTime.of(2025, 5, 5, 19, 10), Duration.ofMinutes(10));
        fileManager.createTask(subTask2);
        fileManager.fillEpic(epic, subTask2);

        SubTask subtask = new SubTask("Мытье полов", "Помыть полы во всех комнатах", Status.NEW);

        fileManager.fillEpic(epic, subtask);
        fileManager.createTask(subtask);

        SubTask subtask2 = new SubTask("Test-1000", "testing testing", Status.NEW,
                LocalDateTime.of(2025, 5, 5, 23, 1), Duration.ofMinutes(54));
        fileManager.fillEpic(epic, subtask2);
        fileManager.createTask(subtask2);
        subtask2 = new SubTask("Test-1000", "testing testing-updated", subtask2.getId(), Status.DONE,
                LocalDateTime.of(2025, 5, 5, 19, 30), Duration.ofMinutes(54));
        fileManager.updateTask(subtask2);

        fileManager.getTaskById(subTask2.getId());
        fileManager.getTaskById(epic.getId());
        fileManager.getTaskById(task.getId());

        System.out.println(fileManager);
        System.out.println(fileManager.getHistoryManager().getHistory());
        System.out.println(fileManager.getPrioritizedTasks());
        System.out.println();

        FileBackedTasksManager fileManagerBacked = loadFromFile(new File("resources/data.csv"));
        System.out.println(fileManagerBacked);
        System.out.println(fileManagerBacked.getHistoryManager().getHistory());
        System.out.println(fileManagerBacked.getPrioritizedTasks());

    }
}
