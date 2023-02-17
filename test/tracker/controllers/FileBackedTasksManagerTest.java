package tracker.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tracker.exceptions.ManagerSaveException;
import tracker.interfaces.TaskManagerTest;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.SubTask;
import tracker.model.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager("resources/data.csv");
    }

    @Test
    public void readFileWhenNoTasksTest() {
        taskManager.save();
        FileBackedTasksManager fromFileManager = FileBackedTasksManager.loadFromFile(new File("resources/data.csv"));

        Assertions.assertEquals(taskManager.getTasks(), fromFileManager.getTasks(), "Коллекции не совпадают");
        Assertions.assertEquals(taskManager.getSubTasks(), fromFileManager.getSubTasks(), "Коллекции не совпадают");
        Assertions.assertEquals(taskManager.getEpics(), fromFileManager.getEpics(), "Коллекции не совпадают");
        Assertions.assertEquals(taskManager.getHistoryManager().getHistory(), fromFileManager.getHistoryManager().getHistory(), "История не совпадает");
        Assertions.assertEquals(taskManager.toString(), fromFileManager.toString(), "Содержимое менеджеров не совпадает");
    }

    @Test
    public void readFileWhenEpicWithoutSubtasksTest() {
        Task task = new Task("Task", "Test task", Status.NEW,
                LocalDateTime.of(2022, 12, 12, 12, 12), Duration.ofMinutes(15));
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        Epic epic = new Epic("Epic", "test epic", Status.NEW);
        taskManager.createTask(epic);
        taskManager.getTaskById(epic.getId());

        FileBackedTasksManager fromFileManager = FileBackedTasksManager.loadFromFile(new File("resources/data.csv"));

        Assertions.assertEquals(taskManager.getSubTasks(), fromFileManager.getSubTasks(), "Коллекции не совпадают");
        Assertions.assertEquals(taskManager.getEpics(), fromFileManager.getEpics(), "Коллекции не совпадают");
        Assertions.assertEquals(taskManager.getHistoryManager().getHistory(), fromFileManager.getHistoryManager().getHistory(), "История не совпадает");
        Assertions.assertEquals(taskManager.toString(), fromFileManager.toString(), "Содержимое менеджеров не совпадает");
    }

    @Test
    public void readFileWithEmptyHistoryTest() {
        Task task = new Task("Task", "test task", Status.NEW);
        Task task2 = new Task("Task2", "test task2", Status.NEW);
        Task task3 = new Task("Task3", "test task3", Status.NEW);

        taskManager.createTask(task);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        FileBackedTasksManager fromFileManager = FileBackedTasksManager.loadFromFile(new File("resources/data.csv"));

        Assertions.assertEquals(taskManager.getHistoryManager().getHistory(), fromFileManager.getHistoryManager().getHistory(), "История не совпадает");
        Assertions.assertEquals(taskManager.toString(), fromFileManager.toString(), "Содержимое менеджеров не совпадает");
    }

    @Test
    public void readFileWithExistentHistoryTest(){
        Task task = new Task("Task", "test task", Status.NEW);
        taskManager.createTask(task);
        Epic epic = new Epic("Epic", "test epic", Status.NEW);
        taskManager.createTask(epic);
        SubTask subTask = new SubTask("Subtask", "test subtask", Status.NEW);
        taskManager.createTask(subTask);
        taskManager.fillEpic(epic, subTask);

        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(subTask.getId());
        taskManager.getTaskById(epic.getId());

        FileBackedTasksManager fromFileManager = FileBackedTasksManager.loadFromFile(new File("resources/data.csv"));

        Assertions.assertEquals(taskManager.getHistoryManager().getHistory(), fromFileManager.getHistoryManager().getHistory(), "История не совпадает");
        Assertions.assertEquals(taskManager.toString(), fromFileManager.toString(), "Содержимое менеджеров не совпадает");
    }

    @Test
    public void readFileWithEpicsEndItsSubtasksTest() {
        Epic epic = new Epic("Epic", "test epic", Status.NEW);
        taskManager.createTask(epic);

        SubTask subTask = new SubTask("Subtask", "test subtask", Status.NEW);
        taskManager.createTask(subTask);
        SubTask subTask2 = new SubTask("Subtask 2", "test subtask 2", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 12, 12, 12, 12), Duration.ofMinutes(15));
        taskManager.createTask(subTask2);

        taskManager.fillEpic(epic, subTask);
        taskManager.fillEpic(epic, subTask2);

        FileBackedTasksManager fromFileManager = FileBackedTasksManager.loadFromFile(new File("resources/data.csv"));

        Assertions.assertEquals(taskManager.getSubTasks(), fromFileManager.getSubTasks(), "Коллекции не совпадают");
        Assertions.assertEquals(taskManager.getEpics(), fromFileManager.getEpics(), "Коллекции не совпадают");
        assertEquals(epic.getStartTime(), fromFileManager.getTaskById(epic.getId()).getStartTime(), "Время начала задачи не совпадает");
        assertEquals(epic.getEndTime(), fromFileManager.getTaskById(epic.getId()).getEndTime(), "Время конца задачи не совпадает");
    }

    @Test
    public void readingFromNonExistentFileShouldThrowException() {
        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTasksManager invalidNManager = FileBackedTasksManager.loadFromFile(new File("nonExistentFile.txt"));
        });
    }

    @Test
    public void savingInFileWithWrongPathnameShouldThrowException() {
        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTasksManager invalidNManager = new FileBackedTasksManager("resources.jar/wrong-path#name.bat<>/..,");
            Task task = new Task("Task", "Test task", Status.NEW);
            invalidNManager.createTask(task);
        });
    }
}
