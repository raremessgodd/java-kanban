import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HttpTaskServer;
import managers.HttpTaskManager;
import managers.TaskManager;
import server.KVServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static HttpClient client;
    private static final String uri = "http://localhost:" + HttpTaskServer.PORT;
    public static void main(String[] args) throws IOException, InterruptedException {
        Task task1, task2;
        Epic epic1, epic2;
        Subtask subtask1, subtask2;

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

        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer server = new HttpTaskServer();
        server.start();

        System.out.println(gson.toJson(task1));
    }

}
