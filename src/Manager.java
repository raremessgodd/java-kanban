import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    int id = 1;
    public HashMap<Integer, Task> allTasks = new HashMap<>();
    public HashMap<Integer, Subtask> allSubtasks = new HashMap<>();
    public HashMap<Integer, Epic> allEpics = new HashMap<>();

    public int setId () {
        return id++;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(allTasks.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(allSubtasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(allEpics.values());
    }

    public void createAnyTask (Object object) {
        switch (object.getClass().getName()) {
            case ("Task") :
                Task task = (Task) object;
                task.taskId = setId();
                allTasks.put(task.taskId, task);
                break;
            case ("Epic") :
                Epic epic = (Epic) object;
                epic.taskId = setId();
                allEpics.put(epic.taskId, epic);
                break;
            case ("Subtask") :
                Subtask subtask = (Subtask) object;
                subtask.taskId = setId();
                allSubtasks.put(subtask.taskId, subtask);
                allEpics.get(subtask.epicId).addSubtask(subtask);
                allEpics.get(subtask.epicId).setStatus();
                break;
        }
    }

    public void deleteAllTasks () {
        allTasks.clear();
        allSubtasks.clear();
        allEpics.clear();
    }

    public void deleteById (int id) {
        if (allTasks.containsKey(id)){
            allTasks.remove(id);
        } else if (allEpics.containsKey(id)) {
            allEpics.remove(id);
        } else {
            allSubtasks.remove(id);
            for (Epic epic : allEpics.values()) {
                epic.subtasks.remove(id);
            }
        }
    }

    public Task getTaskById (int id) {
        return allTasks.get(id);
    }

    public Epic getEpicById (int id) {
        return allEpics.get(id);
    }

    public Subtask getSubtaskById (int id) {
        return allSubtasks.get(id);
    }

    public void updateAnyTask (Object newTask, Integer id) {
        switch (newTask.getClass().getName()) {
            case ("Task") :
                Task task = (Task) newTask;
                task.taskId = id;
                allTasks.put(task.taskId, task);
                break;
            case ("Epic") :
                Epic epic = (Epic) newTask;
                epic.taskId = id;
                epic.subtasks = allEpics.get(id).subtasks;
                epic.setStatus();
                allEpics.put(epic.taskId, epic);
                break;
            case ("Subtask") :
                Subtask subtask = (Subtask) newTask;
                if (allEpics.containsKey(subtask.epicId)) {
                    subtask.taskId = id;
                    allSubtasks.put(subtask.taskId, subtask);
                    allEpics.get(subtask.epicId).addSubtask(subtask);
                    allEpics.get(subtask.epicId).setStatus();
                }
                break;
        }
    }
}
