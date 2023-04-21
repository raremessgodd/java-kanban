package managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import server.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager>{
    private KVServer kvServer;
    private final String uri = "http://localhost:" + KVServer.PORT + "/";

    @BeforeEach
    void startKVServer() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        manager = new HttpTaskManager(uri);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createEpic(epic2);
        manager.createTask(task1);
        manager.createTask(task2);
    }

    @AfterEach
    void stopKVServer() {
        kvServer.stop();
    }

    @Test
    void save() {
        manager.getTaskById(task2.getTaskId());
        ArrayList<Task> allTasks = manager.getAllTasks();
        ArrayList<Subtask> allSubtasks = manager.getAllSubtasks();
        ArrayList<Epic> allEpics = manager.getAllEpics();
        ArrayList<Task> history = manager.getHistory();

        Gson gson = new Gson();

        KVTaskClient kvClient = new KVTaskClient(uri);
        String tasks = kvClient.load("TASKS").orElse(null);
        String subtasks = kvClient.load("SUBTASKS").orElse(null);
        String epics = kvClient.load("EPICS").orElse(null);
        String stringHistory = kvClient.load("HISTORY").orElse(null);
        Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
        ArrayList<Task> testAllTasks = gson.fromJson(tasks, taskType);

        Type subtaskType = new TypeToken<ArrayList<Subtask>>(){}.getType();
        ArrayList<Subtask> testAllSubtasks = gson.fromJson(subtasks, subtaskType);

        Type epicType = new TypeToken<ArrayList<Epic>>(){}.getType();
        ArrayList<Epic> testAllEpics = gson.fromJson(epics, epicType);

        ArrayList<Task> testHistory  = gson.fromJson(stringHistory, taskType);

        assertEquals(allTasks, testAllTasks, "Данные задач загрузились на сервер некорректно.");
        assertEquals(allSubtasks, testAllSubtasks, "Данные подзадач загрузились на сервер некорректно.");
        assertEquals(allEpics, testAllEpics, "Данные эпиков загрузились на сервер некорректно.");
        assertEquals(history, testHistory, "Данные истории загрузились на сервер некорректно.");
    }

    @Test
    void load()  {
        manager.getTaskById(task2.getTaskId());
        ArrayList<Task> allTasks = manager.getAllTasks();
        ArrayList<Subtask> allSubtasks = manager.getAllSubtasks();
        ArrayList<Epic> allEpics = manager.getAllEpics();
        ArrayList<Task> history = manager.getHistory();

        TaskManager testManager = Managers.getDefault();
        ArrayList<Task> testAllTasks = testManager.getAllTasks();
        ArrayList<Subtask> testAllSubtasks = testManager.getAllSubtasks();
        ArrayList<Epic> testAllEpics = testManager.getAllEpics();
        ArrayList<Task> testHistory = testManager.getHistory();

        assertEquals(allTasks, testAllTasks, "Данные задач с сервера загрузились некорректно.");
        assertEquals(allSubtasks, testAllSubtasks, "Данные подзадач с сервера загрузились некорректно.");
        assertEquals(allEpics, testAllEpics, "Данные эпиков с сервера загрузились некорректно.");
        assertEquals(history, testHistory, "Данные истории с сервера загрузились некорректно.");
    }

}