package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest <T extends TaskManager> {
    protected T manager;
    Task task1, task2;
    Epic epic1, epic2;
    Subtask subtask1, subtask2;

    @BeforeEach
    void createTasks() {
        task1 = new Task();
        task2 = new Task();
        epic1 = new Epic();
        epic1.setTaskId(1);
        subtask1 = new Subtask(epic1.getTaskId());
        subtask2 = new Subtask(epic1.getTaskId());
        epic2 = new Epic();

        epic1.setName("epic1");

        epic2.setName("epic2");

        subtask1.setStartTime(LocalDateTime.now().plusDays(10));
        subtask1.setDuration(Duration.ofHours(50));
        subtask1.setStatus(Status.DONE);
        subtask1.setName("subtask1");

        subtask2.setStartTime(LocalDateTime.now().plusDays(20));
        subtask2.setDuration(Duration.ofHours(10));
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setName("subtask2");

        task1.setStartTime(LocalDateTime.now().plusDays(30));
        task1.setDuration(Duration.ofHours(13));
        task1.setStatus(Status.NEW);
        task1.setName("task1");

        task2.setStartTime(LocalDateTime.now().plusDays(40));
        task2.setDuration(Duration.ofHours(40));
        task2.setStatus(Status.DONE);
        task2.setName("task2");
    }

    @Test
    void getHistory() {
        manager.getTaskById(task1.getTaskId());
        manager.getTaskById(task2.getTaskId());

        ArrayList<Task> actualHistory = manager.getHistory();
        ArrayList<Task> expectedHistory = new ArrayList<>();
        expectedHistory.add(0, task1);
        expectedHistory.add(1, task2);

        assertNotNull(actualHistory, "Возвращается пустая история.");
        assertEquals(actualHistory, expectedHistory, "История возвращается некорректно.");
    }

    @Test
    void getAllTasks() {
        ArrayList<Task> actualTasks = manager.getAllTasks();
        ArrayList<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(0, task1);
        expectedTasks.add(1, task2);

        assertNotNull(actualTasks, "Задачи не возвращаются.");
        assertEquals(actualTasks.size(), expectedTasks.size(), "Неверное количество задач.");
        assertEquals(actualTasks.get(0), expectedTasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getAllSubtasks() {
        ArrayList<Subtask> actualSubtasks = manager.getAllSubtasks();
        ArrayList<Subtask> expectedSubtasks = new ArrayList<>();
        expectedSubtasks.add(0, subtask1);
        expectedSubtasks.add(1, subtask2);

        assertNotNull(actualSubtasks, "Подзадачи не возвращаются.");
        assertEquals(actualSubtasks.size(), expectedSubtasks.size(), "Неверное количество подзадач.");
        assertEquals(actualSubtasks.get(0), expectedSubtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void getAllEpics() {
        ArrayList<Epic> actualEpics = manager.getAllEpics();
        ArrayList<Epic> expectedEpics = new ArrayList<>();
        expectedEpics.add(0, epic1);
        expectedEpics.add(1, epic2);

        assertNotNull(actualEpics, "Эпик не возвращаются.");
        assertEquals(actualEpics.size(), expectedEpics.size(), "Неверное количество эпиков.");
        assertEquals(actualEpics.get(0), expectedEpics.get(0), "Эпики не совпадают.");
    }

    @Test
    void createTask() {
        assertNotNull(manager.getAllTasks().get(0), "Задача не найдена.");
        assertEquals(task1, manager.getAllTasks().get(0), "Задачи не совпадаюст.");
    }

    @Test
    void createSubtask() {
        assertNotNull(manager.getAllSubtasks().get(0), "Подзадача не найдена.");
        assertEquals(subtask1, manager.getAllEpics().get(0).getSubtasks().get(subtask1.getTaskId()),
                "Подзадача не связывается с эпиком.");
        assertEquals(subtask1, manager.getAllSubtasks().get(0), "Задачи не совпадают.");
    }

    @Test
    void createEpic() {
        assertNotNull(manager.getAllEpics().get(0), "Эпик не найден.");
        assertEquals(epic1, manager.getAllEpics().get(0), "Эпики не совпадают.");
    }

    @Test
    void deleteAllTasks() {
        manager.deleteAllTasks();
        ArrayList<Task> actualTasks = manager.getAllTasks();

        assertTrue(actualTasks.isEmpty(), "Удаляются не все задачи.");
    }

    @Test
    void deleteAllSubtasks() {
        manager.deleteAllSubtasks();
        ArrayList<Subtask> actualSubtasks = manager.getAllSubtasks();

        assertTrue(epic1.getSubtasks().values().isEmpty(), "Удаляются не все подзадачи внутри эпиков.");
        assertTrue(actualSubtasks.isEmpty(), "Удаляются не все подзадачи.");
    }

    @Test
    void deleteAllEpics() {
        manager.deleteAllEpics();
        ArrayList<Epic> actualEpics = manager.getAllEpics();
        ArrayList<Subtask> actualSubtasks = manager.getAllSubtasks();

        assertTrue(actualSubtasks.isEmpty(), "Удаляются не все подзадачи.");
        assertTrue(actualEpics.isEmpty(), "Удаляются не все эпики.");
    }

    @Test
    void deleteTaskById() {
        manager.deleteTaskById(task1.getTaskId());
        ArrayList<Task> actualTasks = manager.getAllTasks();

        assertFalse(actualTasks.contains(task1), "Задача не удалилась.");
    }

    @Test
    void deleteSubtaskById() {
        manager.deleteSubtaskById(subtask1.getTaskId());
        ArrayList<Subtask> actualSubtasks = manager.getAllSubtasks();

        assertFalse(actualSubtasks.contains(subtask1), "Подзадача не удалилась.");
        assertFalse(epic1.getSubtasks().containsValue(subtask1), "Подзадача не удалилась из эпика.");
    }

    @Test
    void deleteEpicById() {
        manager.deleteEpicById(epic1.getTaskId());
        ArrayList<Epic> actualEpics = manager.getAllEpics();
        ArrayList<Subtask> actualSubtasks = manager.getAllSubtasks();

        assertFalse(actualSubtasks.contains(subtask1), "Подзадача не удалилась.");
        assertFalse(actualSubtasks.contains(subtask2), "Подзадача не удалилась.");
        assertFalse(actualEpics.contains(epic1), "Эпик не удалился.");
    }

    @Test
    void getTaskById() {
        assertEquals(manager.getTaskById(task1.getTaskId()), task1, "Задачи не совпадают.");
    }

    @Test
    void getSubtaskById() {
        assertEquals(manager.getSubtaskById(subtask1.getTaskId()), subtask1, "Подзадачи не совпадают.");
    }

    @Test
    void getEpicById() {
        assertEquals(manager.getEpicById(epic1.getTaskId()), epic1, "Эпики не совпадают.");
    }

    @Test
    void updateTask() {
        Task task3 = new Task();
        task3.setStartTime(LocalDateTime.now());
        task3.setDuration(Duration.ofHours(1));
        manager.updateTask(task3, task1.getTaskId());

        assertEquals(task3.getTaskId(), task1.getTaskId(), "Присвоился не тот ID.");
        assertEquals(manager.getTaskById(task1.getTaskId()), task3, "Задача не обновилась.");
    }

    @Test
    void updateSubtask() {
        Subtask subtask3 = new Subtask(epic1.getTaskId());
        subtask3.setStartTime(LocalDateTime.now());
        subtask3.setDuration(Duration.ofHours(1));
        manager.updateSubtask(subtask3, subtask1.getTaskId());

        assertEquals(subtask3.getTaskId(), subtask1.getTaskId(), "Присвоился не тот ID.");
        assertEquals(manager.getSubtaskById(subtask1.getTaskId()), subtask3, "Подзадача не обновилась.");
        assertEquals(epic1.getSubtasks().get(subtask1.getTaskId()), subtask3, "Подзадача не обновилась внутри эпика.");
    }

    @Test
    void updateEpic() {
        Epic epic3 = new Epic();
        manager.updateEpic(epic3, epic1.getTaskId());

        assertEquals(epic3.getTaskId(), epic1.getTaskId(), "Присвоился не тот ID.");
        assertEquals(manager.getEpicById(epic1.getTaskId()), epic3, "Эпик не обновился.");
        assertEquals(epic3.getSubtasks().get(subtask1.getTaskId()), subtask1, "Подзадачи не вошли в новый эпик.");
    }

    @Test
    void updateEpicStatusAllDone() {
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        manager.updateEpicStatus(epic1);

        assertEquals(epic1.getStatus(), Status.DONE, "Присвоился не тот статус.");
    }

    @Test
    void updateEpicStatusNewAndDone() {
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);
        manager.updateEpicStatus(epic1);

        assertEquals(epic1.getStatus(), Status.IN_PROGRESS, "Присвоился не тот статус.");
    }

    @Test
    void updateEpicStatusAllInProgress() {
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        manager.updateEpicStatus(epic1);

        assertEquals(epic1.getStatus(), Status.IN_PROGRESS, "Присвоился не тот статус.");
    }

    @Test
    void updateEpicStatusAllNew() {
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.NEW);
        manager.updateEpicStatus(epic1);

        assertEquals(epic1.getStatus(), Status.NEW, "Присвоился не тот статус.");
    }
}