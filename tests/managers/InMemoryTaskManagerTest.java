package managers;

import exceptions.EqualTimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{
    @BeforeEach
    void setManager() {
        manager = new InMemoryTaskManager();

        manager.createEpic(epic1);

        subtask1.setStartTime(LocalDateTime.now().plusDays(10));
        subtask1.setDuration(Duration.ofHours(50));
        manager.createSubtask(subtask1);

        subtask2.setStartTime(LocalDateTime.now().plusDays(20));
        subtask2.setDuration(Duration.ofHours(10));
        manager.createSubtask(subtask2);

        task1.setStartTime(LocalDateTime.now().plusDays(30));
        task1.setDuration(Duration.ofHours(13));
        manager.createTask(task1);

        task2.setStartTime(LocalDateTime.now().plusDays(40));
        task2.setDuration(Duration.ofHours(40));
        manager.createTask(task2);

        manager.createEpic(epic2);
    }

    @Test
    void checkIntersection() {
        Task task3 = new Task();
        task3.setStartTime(LocalDateTime.now().plusDays(29));
        task3.setDuration(Duration.ofHours(30));

        final EqualTimeException e = assertThrows(EqualTimeException.class, () -> manager.checkIntersection(task3));
        assertEquals("Две задачи пересекаются по времени: id." +
                task1.getTaskId() + " - id." + task3.getTaskId(), e.getMessage());
    }
}