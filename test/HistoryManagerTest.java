import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.controllers.InMemoryTaskManager;
import tracker.interfaces.TaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.SubTask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryManagerTest {

    TaskManager taskManager;
    Task task, task2;
    Epic epic;
    SubTask subTask, subTask2;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();

        task = new Task("Task-1", "Test task-1", Status.NEW);
        taskManager.createTask(task);

        task2 = new Task("Task-2", "Test task-2", Status.NEW);
        taskManager.createTask(task2);

        epic = new Epic("Epic", "Test epic", Status.NEW);
        taskManager.createTask(epic);

        subTask = new SubTask("Subtask", "test subtask", Status.NEW);
        taskManager.createTask(subTask);

        subTask2 = new SubTask("Subtask 2", "test subtask 2", Status.IN_PROGRESS);
        taskManager.createTask(subTask2);

        taskManager.fillEpic(epic, subTask);
        taskManager.fillEpic(epic, subTask2);
    }

    @Test
    public void shouldReturnEmptyHistory() {
        assertEquals(new ArrayList<Task>(), taskManager.getHistoryManager().getHistory(), "История не пустая");
    }

    @Test
    public void addInHistoryTest() {
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(subTask.getId());
        taskManager.getTaskById(epic.getId());

        assertEquals(new ArrayList<>(List.of(
                task,
                task2,
                subTask,
                epic)), taskManager.getHistoryManager().getHistory(), "Истории не совпадают");
    }

    @Test
    public void duplicationTest() {
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());

        assertEquals(new ArrayList<>(List.of(task)), taskManager.getHistoryManager().getHistory(), "Истории не совпадают");
    }

    @Test
    public void rewriteWithoutDuplicationTest() {
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(subTask.getId());
        taskManager.getTaskById(epic.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(subTask2.getId());

        assertEquals(new ArrayList<>(List.of(
                task,
                subTask,
                epic,
                task2,
                subTask2)), taskManager.getHistoryManager().getHistory(), "Истории не совпадают");
    }

    @Test
    public void removeFromMiddleOfHistoryTest() {
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(subTask.getId());

        taskManager.getHistoryManager().remove(task2.getId());

        assertEquals(new ArrayList<>(List.of(
                task,
                subTask)), taskManager.getHistoryManager().getHistory(), "Истории не совпадают");
    }

    @Test
    public void removeFromBeginningOfHistoryTest() {
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(epic.getId());
        taskManager.getTaskById(subTask.getId());
        taskManager.getTaskById(subTask2.getId());

        taskManager.getHistoryManager().remove(subTask2.getId());

        assertEquals(new ArrayList<>(List.of(
                task,
                task2,
                epic,
                subTask)), taskManager.getHistoryManager().getHistory(), "Истории не совпадают");
    }

    @Test
    public void removeFromEndOfHistoryTest() {
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(epic.getId());
        taskManager.getTaskById(subTask.getId());
        taskManager.getTaskById(subTask2.getId());

        taskManager.getHistoryManager().remove(task.getId());

        assertEquals(new ArrayList<>(List.of(
                task2,
                epic,
                subTask,
                subTask2)), taskManager.getHistoryManager().getHistory(), "Истории не совпадают");
    }

    @Test
    public void epicDeletionAlsoMustDeleteItsSubtasksFromHistoryTest() {
        taskManager.getTaskById(epic.getId());
        taskManager.getTaskById(subTask.getId());
        taskManager.getTaskById(subTask2.getId());

        taskManager.getHistoryManager().remove(epic.getId());

        assertEquals(new ArrayList<>(), taskManager.getHistoryManager().getHistory());
    }


    @Test
    public void removeFromHistoryNonExistentTaskTest() {
        taskManager.getTaskById(task2.getId());

        assertEquals(new ArrayList<>(List.of(task2)), taskManager.getHistoryManager().getHistory(), "Истории не совпадают");

        taskManager.getHistoryManager().remove(task.getId());
        taskManager.getHistoryManager().remove(-1000);

        assertEquals(new ArrayList<>(List.of(task2)), taskManager.getHistoryManager().getHistory(), "Истории не совпадают");
    }

}