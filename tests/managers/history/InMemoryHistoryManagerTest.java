package managers.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager manager;
    private Task task1, task2;

    @BeforeEach
    void createTasksAndSetManager() {
        manager = new InMemoryHistoryManager();

        task1 = new Task();
        task1.setTaskId(1);

        task2 = new Task();
        task2.setTaskId(2);
    }

    @Test
    void linkLast() {
        manager.linkLast(task1);
        List<Task> actualTasks = manager.getTasks();
        assertNotNull(actualTasks);
        assertEquals(task1, actualTasks.get(0), "Задача не добавляется в историю.");
    }

    @Test
    void removeById() {
        manager.linkLast(task1);
        manager.removeById(task1.getTaskId());
        List<Task> actualTasks = manager.getTasks();
        assertTrue(actualTasks.isEmpty(), "Задача не удаляется.");
    }

    @Test
    void getTasks() {
        manager.linkLast(task1);
        manager.linkLast(task2);
        List<Task> actualTasks = manager.getTasks();
        assertNotNull(actualTasks);
        assertEquals(task1, actualTasks.get(0), "Возвращается не та задача.");
        assertEquals(task2, actualTasks.get(1), "Возвращается не та задача.");
    }

    @Test
    void historyToString() {
        manager.linkLast(task1);
        manager.linkLast(task2);
        String actualString = InMemoryHistoryManager.historyToString(manager);
        String expectedString = task1.getTaskId() + "," + task2.getTaskId() + ",";
        assertNotNull(actualString);
        assertEquals(expectedString, actualString, "История возвращается в неправильном формате.");
    }

    @Test
    void historyFromString() {
        String s = task1.getTaskId() + "," + task2.getTaskId() + ",";
        List<Integer> actualList = InMemoryHistoryManager.historyFromString(s);
        assertNotNull(actualList);
        assertEquals(actualList.get(0), task1.getTaskId(), "Задача не добавляется в иторию.");
        assertEquals(actualList.get(1), task2.getTaskId(), "Задача не добавляется в иторию.");
    }
}