package manager;

import java.util.ArrayList;
import java.util.HashMap;
import tasks.*;


public class Manager {
    int id = 1;
    private HashMap<Integer, Task> allTasks = new HashMap<>();
    private HashMap<Integer, Subtask> allSubtasks = new HashMap<>();
    private HashMap<Integer, Epic> allEpics = new HashMap<>();

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

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        return new ArrayList<>(allEpics.get(epicId).getSubtasks().values());
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
        updateEpicStatus(allEpics.get(subtask.getEpicId()));
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
            updateEpicStatus(epic);
        }
    }

    public void deleteTaskById (int id) {
        allTasks.remove(id);
    }

    public void deleteEpicById (int id) {
        for (Subtask subtask : getEpicSubtasks(id)) {
            allSubtasks.remove(subtask.getTaskId());
        }
        allEpics.remove(id);
    }

    public void deleteSubtaskById (int id) {
        for (Epic epic : allEpics.values()) {
            if (epic.getTaskId() == allSubtasks.get(id).getEpicId()) {
                epic.removeSubtask(id);
                updateEpicStatus(allEpics.get(epic.getTaskId()));
            }
        }
        allSubtasks.remove(id);
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
        updateEpicStatus(newEpic);
    }

    public void updateSubtask (Subtask newSubtask, int id) {
        newSubtask.setTaskId(id);
        allSubtasks.put(newSubtask.getTaskId(), newSubtask);
        allEpics.get(newSubtask.getEpicId()).addSubtask(newSubtask);
        updateEpicStatus(allEpics.get(newSubtask.getEpicId()));
    }

    public void updateEpicStatus(Epic epic) {
        int newStatus = 0;
        int doneStatus = 0;

        for (Subtask subtasks : epic.getSubtasks().values()) {
            if (subtasks.getStatus().equals("DONE")) {
                doneStatus++;
            } else if (subtasks.getStatus().equals("NEW")) {
                newStatus++;
            }
        }

        if (newStatus == epic.getSubtasks().size() || epic.getSubtasks().isEmpty()) {
            epic.setStatus("NEW");
        } else if (doneStatus == epic.getSubtasks().size()) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
    }
}
