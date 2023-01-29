import java.util.HashMap;

public class Manager {
    int id = 1;
    private HashMap<Integer, Task> allTasks = new HashMap<>();
    private HashMap<Integer, Subtask> allSubtasks = new HashMap<>();
    private HashMap<Integer, Epic> allEpics = new HashMap<>();

    public int setId () {
        return id++;
    }

    public HashMap<Integer, Task> getAllTasks() {
        return allTasks;
    }

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return allSubtasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return allEpics;
    }

    public void createTask (Task task) {
        task.setTaskId(setId());
        allTasks.put(task.getTaskId(), task);
    }

    public void createEpic (Epic epic) {
        epic.setTaskId(setId());
        allEpics.put(epic.getTaskId(), epic);
    }

    public void createSubtask (Subtask subtask) {
        subtask.setTaskId(setId());
        allSubtasks.put(subtask.getTaskId(), subtask);
        allEpics.get(subtask.getEpicId()).addSubtask(subtask);
    }

    public void deleteAllTasks () {
        allTasks.clear();
    }

    public void deleteAllEpics () {
        allEpics.clear();
        allSubtasks.clear();
    }

    public void deleteAllSubtasks () {
        allSubtasks.clear();
        for (Epic epic : allEpics.values()) {
            epic.getSubtasks().clear();
            epic.updateStatus();
        }
    }

    public void deleteTaskById (int id) {
        allTasks.remove(id);
    }

    public void deleteEpicById (int id) {
        for (Integer subtaskId : allEpics.get(id).getSubtasks().keySet()) {
            allSubtasks.remove(subtaskId);
        }
        allEpics.remove(id);
    }

    public void deleteSubtaskById (int id) {
        allSubtasks.remove(id);
        for (Epic epic : allEpics.values()) {
            epic.removeSubtask(id);
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

    public void updateTask (Task newTask, int id) {
        newTask.setTaskId(id);
        allTasks.put(newTask.getTaskId(), newTask);
    }

    public void  updateEpic (Epic newEpic, int id) {
        newEpic.setTaskId(id);
        for (Subtask subtask : allEpics.get(id).getSubtasks().values()) {
            subtask.setThisEpic(newEpic);
            newEpic.addSubtask(subtask);
        }
        allEpics.put(newEpic.getTaskId(), newEpic);
        newEpic.updateStatus();
    }

    public void updateSubtask (Subtask newSubtask, int id) {
        newSubtask.setTaskId(id);
        allSubtasks.put(newSubtask.getTaskId(), newSubtask);
        allEpics.get(newSubtask.getEpicId()).addSubtask(newSubtask);
    }
}
