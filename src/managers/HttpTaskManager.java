package managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HttpTaskManager extends FileBackedTaskManager {
    private final KVTaskClient client;
    public HttpTaskManager(String url) {
        super(url);
        client = new KVTaskClient(url);
        load();
    }

    private void load() {
        Gson gson = new Gson();

        String tasks = client.load("TASKS");
        String subtasks = client.load("SUBTASKS");
        String epics = client.load("EPICS");
        String stringHistory = client.load("HISTORY");
        Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
        ArrayList<Task> allTasks = gson.fromJson(tasks, taskType);

        Type subtaskType = new TypeToken<ArrayList<Subtask>>(){}.getType();
        ArrayList<Subtask> allSubtasks = gson.fromJson(subtasks, subtaskType);

        Type epicType = new TypeToken<ArrayList<Epic>>(){}.getType();
        ArrayList<Epic> allEpics = gson.fromJson(epics, epicType);

        ArrayList<Task> history  = gson.fromJson(stringHistory, taskType);

        if (allTasks != null) {
            allTasks.forEach(task -> this.allTasks.put(task.getTaskId(), task));
        }
        if (allSubtasks != null) {
            allSubtasks.forEach(subtask -> this.allSubtasks.put(subtask.getTaskId(), subtask));
        }
        if (allEpics != null) {
            allEpics.forEach(epic -> this.allEpics.put(epic.getTaskId(), epic));
        }
        if (history != null) {
            history.forEach(this.history::linkLast);
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
