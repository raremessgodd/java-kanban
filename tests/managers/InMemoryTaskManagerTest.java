package managers;

import exceptions.EqualTimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{
    @BeforeEach
    void setManager() {
        manager = new InMemoryTaskManager();

        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic2);
    }

    @Test
    void checkIntersection() {
        Task task3 = new Task();
        task3.setStartTime(LocalDateTime.now().plusDays(29));
        task3.setDuration(Duration.ofHours(30));

        final EqualTimeException e = assertThrows(EqualTimeException.class, () -> manager.checkIntersection(task3));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        assertEquals("Задачи пересекаются по времени: " +
                task1.getStartTime().format(formatter) + " -> " + task1.getEndTime().format(formatter) + " ∩ "
                + task3.getStartTime().format(formatter) + " -> " + task3.getEndTime().format(formatter), e.getMessage());
    }
}