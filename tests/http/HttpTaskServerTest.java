package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private HttpClient client;
    private KVServer kvServer;
    private HttpTaskServer server;
    private final String uri = "http://localhost:" + HttpTaskServer.PORT;
    private final Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
    private final Type subtaskType = new TypeToken<ArrayList<Subtask>>(){}.getType();
    private final Type epicType = new TypeToken<ArrayList<Epic>>(){}.getType();
    private final Gson gson = new Gson();
    private Task task1, task2;
    private Epic epic1, epic2;
    private Subtask subtask1, subtask2;

    private void createTask(Task task) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(URI.create(uri + "/tasks/task"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void createSubtask(Subtask subtask) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .uri(URI.create(uri + "/tasks/subtask"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void createEpic(Epic epic) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(URI.create(uri + "/tasks/epic"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void setIds() {
        epic1.setTaskId(1);
        epic2.setTaskId(2);
        task1.setTaskId(3);
        task2.setTaskId(4);
        subtask1.setTaskId(5);
        subtask2.setTaskId(6);
        epic1.addSubtask(subtask1);
        epic1.addSubtask(subtask2);
    }

    private void setTasks() throws IOException, InterruptedException {
        epic1 = new Epic();
        epic2 = new Epic();
        task1 = new Task();
        task2 = new Task();
        subtask1 = new Subtask(1);
        subtask2 = new Subtask(1);

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

        createEpic(epic1);
        createEpic(epic2);
        createTask(task1);
        createTask(task2);
        createSubtask(subtask1);
        createSubtask(subtask2);
    }

    @BeforeEach
    void startServers() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer();
        server.start();
        client = HttpClient.newHttpClient();
        setTasks();
        setIds();
    }

    @AfterEach
    void stopServers() {
        server.stop();
        kvServer.stop();
    }


    @Test
    void getPrioritizedTasks() {
        try {
            HttpRequest testRequest = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks")).build();
            HttpResponse<String> testResponse = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
            ArrayList<Task> actualTasks = gson.fromJson(testResponse.body(), taskType);

            for (int i = 0; i < actualTasks.size() - 1; i++) {
                LocalDateTime taskStart1 = actualTasks.get(i).getStartTime();
                LocalDateTime taskStart2 = actualTasks.get(i + 1).getStartTime();
                LocalDateTime taskEnd1 = actualTasks.get(i).getEndTime();
                LocalDateTime taskEnd2 = actualTasks.get(i + 1).getEndTime();
                assertFalse(!taskStart2.isAfter(taskEnd1) &&
                        !taskEnd2.isBefore(taskStart1));
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void getHistory() {
        try {
            client.send(HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/task?id=3")).build(),
                    HttpResponse.BodyHandlers.ofString());
            client.send(HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/task?id=4")).build(),
                    HttpResponse.BodyHandlers.ofString());

            HttpRequest testRequest = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/history/")).build();
            HttpResponse<String> testResponse = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
            ArrayList<Task> history = gson.fromJson(testResponse.body(), taskType);

            ArrayList<Task> expectedHistory = new ArrayList<>();
            expectedHistory.add(0, task1);
            expectedHistory.add(1, task2);

            assertNotNull(history, "Возвращается пустая история.");
            assertEquals(history, expectedHistory, "История возвращается некорректно.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void getAllTasks() {
        try {
            HttpRequest testRequest = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/task/")).build();
            HttpResponse<String> testResponse = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
            ArrayList<Task> actualTasks = gson.fromJson(testResponse.body(), taskType);
            ArrayList<Task> expectedTasks = new ArrayList<>();
            expectedTasks.add(0, task1);
            expectedTasks.add(1, task2);

            assertNotNull(actualTasks, "Задачи не возвращаются.");
            assertEquals(expectedTasks.size(), actualTasks.size(), "Неверное количество задач.");
            assertEquals(expectedTasks.get(0), actualTasks.get(0), "Задачи не совпадают.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void getAllSubtasks() {
        try {
            HttpRequest testRequest = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/subtask/")).build();
            HttpResponse<String> testResponse = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
            ArrayList<Subtask> actualSubtasks = gson.fromJson(testResponse.body(), subtaskType);
            ArrayList<Subtask> expectedSubtasks = new ArrayList<>();
            expectedSubtasks.add(0, subtask1);
            expectedSubtasks.add(1, subtask2);

            assertNotNull(actualSubtasks, "Подзадачи не возвращаются.");
            assertEquals(actualSubtasks.size(), expectedSubtasks.size(), "Неверное количество подзадач.");
            assertEquals(actualSubtasks.get(0), expectedSubtasks.get(0), "Подзадачи не совпадают.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void getAllEpics() {
        try {
            HttpRequest testRequest = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/epic/")).build();
            HttpResponse<String> testResponse = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
            ArrayList<Epic> actualEpics = gson.fromJson(testResponse.body(), epicType);

            ArrayList<Epic> expectedEpics = new ArrayList<>();
            epic1.addSubtask(subtask1);
            epic1.addSubtask(subtask2);
            expectedEpics.add(0, epic1);
            expectedEpics.add(1, epic2);

            assertNotNull(actualEpics, "Эпик не возвращаются.");
            assertEquals(expectedEpics.size(), actualEpics.size(), "Неверное количество эпиков.");
            assertEquals(expectedEpics.get(0), actualEpics.get(0), "Эпики не совпадают.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void createTask() {
        try {
            Task task = new Task();
            task.setStartTime(LocalDateTime.now().minusDays(30));
            task.setDuration(Duration.ofHours(13));
            task.setStatus(Status.NEW);
            task.setName("testTask");

            createTask(task);
            task.setTaskId(7);

            HttpRequest taskRequest = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/task?id=7")).build();
            HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());
            Task actualTask = gson.fromJson(taskResponse.body(), Task.class);

            assertNotNull(actualTask, "Задача не найдена.");
            assertEquals(task, actualTask, "Задачи не совпадают.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void createSubtask() {
        try {
            Subtask subtask = new Subtask(2);
            subtask.setStartTime(LocalDateTime.now().minusDays(30));
            subtask.setDuration(Duration.ofHours(13));
            subtask.setStatus(Status.NEW);
            subtask.setName("testSubtask");

            createSubtask(subtask);
            subtask.setTaskId(7);

            HttpRequest subtaskRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(uri + "/tasks/subtask?id=" + subtask.getTaskId()))
                    .build();
            HttpResponse<String> subtaskResponse = client.send(subtaskRequest, HttpResponse.BodyHandlers.ofString());
            Subtask actualSubtask = gson.fromJson(subtaskResponse.body(), Subtask.class);

            HttpRequest epicRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(uri + "/tasks/epic/subtask?id=2"))
                    .build();
            HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
            ArrayList<Subtask> epicSubtasks = gson.fromJson(epicResponse.body(), subtaskType);

            assertNotNull(actualSubtask, "Подзадача не найдена.");
            assertTrue(epicSubtasks.contains(subtask), "Подзадача не связывается с эпиком.");
            assertEquals(subtask, actualSubtask, "Задачи не совпадают.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void createEpic() {
        try {
            Epic epic = new Epic();
            epic.setName("testEpic");

            createEpic(epic);
            epic.setTaskId(7);

            HttpRequest epicRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(uri + "/tasks/epic?id=" + epic.getTaskId()))
                    .build();
            HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
            Epic actualEpic = gson.fromJson(epicResponse.body(), Epic.class);

            assertNotNull(actualEpic, "Эпик не найден.");
            assertEquals(epic, actualEpic, "Эпики не совпадают.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void deleteAllTasks() {
        try {
            HttpRequest request = HttpRequest.newBuilder().DELETE().uri(URI.create(uri + "/tasks/task")).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

            HttpRequest testRequest = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/task/")).build();
            HttpResponse<String> testResponse = client.send(testRequest, HttpResponse.BodyHandlers.ofString());

            assertEquals("Список задач пуст." , testResponse.body(), "Удаляются не все задачи.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void deleteAllSubtasks() {
        try {
            HttpRequest request = HttpRequest.newBuilder().DELETE().uri(URI.create(uri + "/tasks/subtask")).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

            HttpRequest testRequest = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/subtask")).build();
            HttpResponse<String> testResponse = client.send(testRequest, HttpResponse.BodyHandlers.ofString());

            HttpRequest testRequest2 = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(uri + "/tasks/epic/subtask?id=" + epic1.getTaskId()))
                    .build();
            HttpResponse<String> testResponse2 = client.send(testRequest2, HttpResponse.BodyHandlers.ofString());

            assertEquals("Список подзадач пуст.", testResponse.body(),"Удаляются не все задачи.");
            assertEquals("У этого эпика пока нет подзадач.", testResponse2.body(), "Удаляются не все подзадачи внутри эпиков.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void deleteAllEpics() {
        try {
            HttpRequest request = HttpRequest.newBuilder().DELETE().uri(URI.create(uri + "/tasks/epic")).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

            HttpRequest testRequest = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/epic")).build();
            HttpResponse<String> testResponse = client.send(testRequest, HttpResponse.BodyHandlers.ofString());

            assertEquals("Список эпиков пуст.", testResponse.body(),"Удаляются не все эпики.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void deleteTaskById() {
        try {
            HttpRequest taskRequest = HttpRequest.newBuilder().DELETE().uri(URI.create(uri + "/tasks/task?id=" + task1.getTaskId())).build();
            HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());

            HttpRequest taskRequest2 = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/task?id=" + task1.getTaskId())).build();
            HttpResponse<String> taskResponse2 = client.send(taskRequest2, HttpResponse.BodyHandlers.ofString());

            assertEquals("Задача удалена.", taskResponse.body(), "Задача не удаляется.");
            assertEquals("Задача не найдена.", taskResponse2.body(), "Задача не удаляется.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void deleteSubtaskById() {
        try {
            HttpRequest taskRequest = HttpRequest.newBuilder().DELETE().uri(URI.create(uri + "/tasks/subtask?id=" + subtask1.getTaskId())).build();
            HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());

            HttpRequest taskRequest2 = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/subtask?id=" + subtask1.getTaskId())).build();
            HttpResponse<String> taskResponse2 = client.send(taskRequest2, HttpResponse.BodyHandlers.ofString());

            HttpRequest taskRequest3 = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/epic/subtask?id=" + epic1.getTaskId())).build();
            HttpResponse<String> taskResponse3 = client.send(taskRequest3, HttpResponse.BodyHandlers.ofString());
            ArrayList<Subtask> subtasks = gson.fromJson(taskResponse3.body(), subtaskType);

            assertEquals("Подзадача удалена.", taskResponse.body(), "Подзадача не удаляется.");
            assertEquals("Подзадача не найдена.", taskResponse2.body(), "Подзадача не удаляется.");
            assertFalse(subtasks.contains(subtask1), "Подзадача не удаояется из эпика.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void deleteEpicById() {
        try {
            HttpRequest taskRequest = HttpRequest.newBuilder().DELETE().uri(URI.create(uri + "/tasks/epic?id=" + epic1.getTaskId())).build();
            HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());

            HttpRequest taskRequest2 = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/epic?id=" + epic1.getTaskId())).build();
            HttpResponse<String> taskResponse2 = client.send(taskRequest2, HttpResponse.BodyHandlers.ofString());

            assertEquals("Эпик удален.", taskResponse.body(), "Эпик не удаляется.");
            assertEquals("Эпик не найден.", taskResponse2.body(), "Эпик не удаляется.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void getTaskById() {
        try {
            HttpRequest taskRequest = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/task?id=" + task1.getTaskId())).build();
            HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());
            Task actualTask = gson.fromJson(taskResponse.body(), Task.class);

            assertNotNull(actualTask, "Задача не возвращается.");
            assertEquals(task1, actualTask, "Задачи не совпадают.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void getSubtaskById() {
        try {
            HttpRequest taskRequest = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/subtask?id=" + subtask1.getTaskId())).build();
            HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());
            Subtask actualSubtask = gson.fromJson(taskResponse.body(), Subtask.class);

            assertNotNull(actualSubtask, "Подзадача не возвращается.");
            assertEquals(subtask1, actualSubtask, "Подзадачи не совпадают.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void getEpicById() {
        try {
            HttpRequest taskRequest = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/epic?id=" + epic1.getTaskId())).build();
            HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());
            Epic actualEpic = gson.fromJson(taskResponse.body(), Epic.class);

            assertNotNull(actualEpic, "Эпик не возвращается.");
            assertEquals(epic1, actualEpic, "Эпики не совпадают.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void updateTask() {
        try {
            Task task = new Task();
            task.setTaskId(task1.getTaskId());
            task.setStartTime(LocalDateTime.now().minusMonths(1));
            task.setDuration(Duration.ofHours(1));
            task.setStatus(Status.IN_PROGRESS);
            task.setName("testTask");

            createTask(task);

            HttpRequest taskRequest = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/task?id=" + task1.getTaskId())).build();
            HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());
            Task actualTask = gson.fromJson(taskResponse.body(), Task.class);

            assertEquals(task, actualTask, "Задача не обновилась.");
        }  catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void updateSubtask() {
        try {
            Subtask subtask = new Subtask(epic2.getTaskId());
            subtask.setTaskId(subtask1.getTaskId());
            subtask.setStartTime(LocalDateTime.now().minusDays(30));
            subtask.setDuration(Duration.ofHours(13));
            subtask.setStatus(Status.NEW);
            subtask.setName("testSubtask");

            createSubtask(subtask);

            HttpRequest subtaskRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(uri + "/tasks/subtask?id=" + subtask1.getTaskId()))
                    .build();
            HttpResponse<String> subtaskResponse = client.send(subtaskRequest, HttpResponse.BodyHandlers.ofString());
            Subtask actualSubtask = gson.fromJson(subtaskResponse.body(), Subtask.class);

            HttpRequest epicRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(uri + "/tasks/epic?id=" + epic2.getTaskId()))
                    .build();
            HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
            Epic epic = gson.fromJson(epicResponse.body(), Epic.class);

            assertEquals(subtask, actualSubtask, "Подзадача не обновилась.");
            assertEquals(epic.getSubtasks().get(subtask1.getTaskId()), subtask, "Подзадача не обновилась внутри эпика.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }

    @Test
    void updateEpic() {
        try {
            Epic epic = new Epic();
            epic.setTaskId(epic1.getTaskId());
            epic.setName("testEpic");

            createEpic(epic);
            epic.addSubtask(subtask1);
            epic.addSubtask(subtask2);

            HttpRequest epicRequest = HttpRequest.newBuilder().GET().uri(URI.create(uri + "/tasks/epic?id=" + epic1.getTaskId())).build();
            HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
            Epic actualEpic = gson.fromJson(epicResponse.body(), Epic.class);

            assertEquals(epic, actualEpic, "Задача не обновилась.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка. " + e.getMessage());
            fail();
        }
    }
}