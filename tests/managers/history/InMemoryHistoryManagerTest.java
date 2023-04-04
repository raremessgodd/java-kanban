package managers.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager manager;
    Task task1, task2;
    Epic epic1, epic2;
    Subtask subtask1, subtask2;

    @BeforeEach
    void createTasksAndSetManager() {
        manager = new InMemoryHistoryManager();

        task1 = new Task();
        task1.setTaskId(1);

        task2 = new Task();
        task2.setTaskId(2);

        epic1 = new Epic();
        epic1.setTaskId(3);

        subtask1 = new Subtask(epic1.getTaskId());
        subtask1.setTaskId(4);

        subtask2 = new Subtask(epic1.getTaskId());
        subtask2.setTaskId(5);

        epic2 = new Epic();
        epic2.setTaskId(6);
    }

    @BeforeEach
    public void setManager() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    void linkLast() {
        manager.linkLast(task1);
        List<Task> actualTasks = manager.getTasks();
        assertNotNull(actualTasks);
        assertEquals(task1, actualTasks.get(0));
    }

    @Test
    void removeById() {
        manager.linkLast(task1);
        manager.removeById(task1.getTaskId());
        List<Task> actualTasks = manager.getTasks();
        assertTrue(actualTasks.isEmpty());
    }

    @Test
    void getTasks() {
        manager.linkLast(task1);
        manager.linkLast(task2);
        List<Task> actualTasks = manager.getTasks();
        assertNotNull(actualTasks);
        assertEquals(task1, actualTasks.get(0));
        assertEquals(task2, actualTasks.get(1));
    }

    @Test
    void historyToString() {
        manager.linkLast(task1);
        manager.linkLast(task2);
        String actualString = InMemoryHistoryManager.historyToString(manager);
        String expectedString = task1.getTaskId() + "," + task2.getTaskId() + ",";
        assertNotNull(actualString);
        assertEquals(expectedString, actualString);
    }

    @Test
    void historyFromString() {
        String s = task1.getTaskId() + "," + task2.getTaskId() + ",";
        List<Integer> actualList = InMemoryHistoryManager.historyFromString(s);
        assertNotNull(actualList);
        assertEquals(actualList.get(0), task1.getTaskId());
        assertEquals(actualList.get(1), task2.getTaskId());
    }
}