package tracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.controllers.InMemoryTaskManager;
import tracker.interfaces.TaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {

    private Epic epic;
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        epic = new Epic("Epic", "Test epic", Status.NEW);
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void epicWithoutSubtasksShouldHaveNewStatus() {
        assertEquals(Status.NEW, epic.getStatus(), "Статус не совпадает");
    }

    @Test
    public void epicWithAllNewSubtasksShouldHaveNewStatus() {
        SubTask subtask = new SubTask("Subtask-1", "Test subtask-1", Status.NEW);
        SubTask subtask2 = new SubTask("Subtask-2", "Test subtask-2", Status.NEW);
        taskManager.createTask(subtask);
        taskManager.createTask(subtask2);

        taskManager.fillEpic(epic, subtask);
        taskManager.fillEpic(epic, subtask2);

        assertEquals(Status.NEW, epic.getStatus(), "Статус не совпадает");
    }

    @Test
    public void epicWithAllDoneSubtasksShouldHaveDoneStatus() {
        SubTask subtask = new SubTask("Subtask-1", "Test subtask-1", Status.DONE);
        SubTask subtask2 = new SubTask("Subtask-2", "Test subtask-2", Status.DONE);
        taskManager.createTask(subtask);
        taskManager.createTask(subtask2);

        taskManager.fillEpic(epic, subtask);
        taskManager.fillEpic(epic, subtask2);

        assertEquals(Status.DONE, epic.getStatus(), "Статус не совпадает");
    }

    @Test
    public void epicWithNewAndDoneSubtasksShouldHaveInProgressStatus() {
        SubTask subtask = new SubTask("Subtask-1", "Test subtask-1", Status.NEW);
        SubTask subtask2 = new SubTask("Subtask-2", "Test subtask-2", Status.DONE);
        taskManager.createTask(subtask);
        taskManager.createTask(subtask2);

        taskManager.fillEpic(epic, subtask);
        taskManager.fillEpic(epic, subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус не совпадает");
    }

    @Test
    public void epicWithAllInProgressSubtasksShouldHaveInProgresStatus() {
        SubTask subtask = new SubTask("Subtask-1", "Test subtask-1", Status.IN_PROGRESS);
        SubTask subtask2 = new SubTask("Subtask-2", "Test subtask-2", Status.IN_PROGRESS);
        taskManager.createTask(subtask);
        taskManager.createTask(subtask2);

        taskManager.fillEpic(epic, subtask);
        taskManager.fillEpic(epic, subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус не совпадает");
    }
}
