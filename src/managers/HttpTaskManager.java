package managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class HttpTaskManager extends FileBackedTaskManager {
    private final KVTaskClient client;
    public HttpTaskManager(String url) {
        super(url);
        client = new KVTaskClient(url);
        load();
    }

    private void load() {
        Gson gson = new Gson();

        String tasks = client.load("TASKS").orElse(null);
        String subtasks = client.load("SUBTASKS").orElse(null);
        String epics = client.load("EPICS").orElse(null);
        String stringHistory = client.load("HISTORY").orElse(null);
        Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();

        Type subtaskType = new TypeToken<ArrayList<Subtask>>(){}.getType();

        Type epicType = new TypeToken<ArrayList<Epic>>(){}.getType();

        deserializeAndSet(gson, tasks, taskType, allTasks::put);
        deserializeAndSet(gson, subtasks, subtaskType, allSubtasks::put);
        deserializeAndSet(gson, epics, epicType, allEpics::put);
        deserializeAndSet(gson, stringHistory, taskType, (BiConsumer<Integer, Task>) (integer, task) -> history.linkLast(task));
    }

    private <T> void deserializeAndSet(Gson gson, String json, Type type, BiConsumer<Integer, T> consumer) {
        ArrayList<T> list = gson.fromJson(json, type);
        if (list != null) {
            list.forEach(item -> consumer.accept(((Task) item).getTaskId(), item));
        }
    }

    @Override
    public void save() {
        Gson gson = new Gson();
        client.put("TASKS", gson.toJson(getAllTasks()));
        client.put("SUBTASKS", gson.toJson(getAllSubtasks()));
        client.put("EPICS", gson.toJson(getAllEpics()));
        client.put("HISTORY", gson.toJson(getHistory()));
    }
}
