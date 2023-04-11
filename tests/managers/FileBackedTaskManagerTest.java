package managers;

import org.junit.jupiter.api.*;
import tasks.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager>{

    Path path = Path.of("tests/testsData/test_tasks_data.csv");
    @BeforeEach
    void setManager() {
        manager = new FileBackedTaskManager(path.toString());

        manager.createEpic(epic1);

        subtask1.setStartTime(LocalDateTime.now().plusDays(10));
        subtask1.setDuration(Duration.ofHours(50));
        subtask1.setStatus(Status.DONE);
        manager.createSubtask(subtask1);

        subtask2.setStartTime(LocalDateTime.now().plusDays(20));
        subtask2.setDuration(Duration.ofHours(10));
        subtask2.setStatus(Status.IN_PROGRESS);
        manager.createSubtask(subtask2);

        manager.createEpic(epic2);

        task1.setStartTime(LocalDateTime.now().plusDays(30));
        task1.setDuration(Duration.ofHours(13));
        task1.setStatus(Status.NEW);
        manager.createTask(task1);

        task2.setStartTime(LocalDateTime.now().plusDays(40));
        task2.setDuration(Duration.ofHours(40));
        task2.setStatus(Status.DONE);
        manager.createTask(task2);
    }

    @Test
    void save()  {
        manager.getTaskById(task2.getTaskId());

        try {
            List<String> values = Files.readAllLines(path);

            assertEquals("id,type,name,status,startTime,duration,description,epic,", values.get(0),
                    "Неверная запись титульной строки в файл.");
            assertEquals(task1.toString(), values.get(1), "Неверная запись задачи в файл.");
            assertEquals(subtask1.toString(), values.get(5), "Неверная запись подзадачи в файл.");
            assertEquals(epic1.toString(), values.get(3), "Неверная запись эпика в файл.");
            assertEquals(task2.getTaskId() + ",", values.get(values.size() - 1), "Неверная запись истории в файл.");
        } catch (IOException e) {
            throw new RuntimeException("Неверный путь к файлу. ", e);
        }
    }

    @Test
    void loadFromFile() {
        FileBackedTaskManager testManager = FileBackedTaskManager.loadFromFile(path.toFile());

        assertEquals(testManager.getTaskById(task1.getTaskId()).toString(), task1.toString(),
                "Не получается преобразовать строку в задачу.");
        assertEquals(testManager.getSubtaskById(subtask1.getTaskId()).toString(), subtask1.toString(),
                "Не получается преобразовать строку в подзадачу.");
        assertEquals(testManager.getEpicById(epic1.getTaskId()).toString(), epic1.toString(),
                "Не получается преобразовать строку в эпик.");
        assertEquals(testManager.getEpicById(epic1.getTaskId()).getSubtasks().get(subtask1.getTaskId()).toString(), subtask1.toString(),
                "Подзадача не кладется в эпик.");
        assertEquals(testManager.getHistory().get(0).toString(), task1.toString(), "Не получается преобразовать строку в историю.");

    }
}