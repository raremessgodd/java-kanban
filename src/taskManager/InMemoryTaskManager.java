package taskManager;

import java.util.ArrayList;
import java.util.HashMap;

import managers.Managers;
import tasks.*;
import historyManager.*;


public class InMemoryTaskManager implements TaskManager {
    int id = 1;

    private final HistoryManager history = Managers.getDefaultHistory();
    private final HashMap<Integer, Task> allTasks = new HashMap<>();
    private final HashMap<Integer, Subtask> allSubtasks = new HashMap<>();
    private final HashMap<Integer, Epic> allEpics = new HashMap<>();

    public int setId () {
        return id++;
    }

    @Override
    public ArrayList<Task> getHistory(){
        return history.getHistory();
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

    @Override
    public void createTask (Task task) {
        task.setTaskId(setId());
        allTasks.put(task.getTaskId(), task);
    }

    @Override
    public void createEpic (Epic epic) {
        epic.setTaskId(setId());
        allEpics.put(epic.getTaskId(), epic);
    }

    @Override
    public void createSubtask (Subtask subtask) {
        subtask.setTaskId(setId());
        allSubtasks.put(subtask.getTaskId(), subtask);
        allEpics.get(subtask.getEpicId()).addSubtask(subtask);
        updateEpicStatus(allEpics.get(subtask.getEpicId()));
    }

    @Override
    public void deleteAllTasks () {
        allTasks.clear();
    }

    @Override
    public void deleteAllEpics () {
        allEpics.clear();
        allSubtasks.clear();
    }

    @Override
    public void deleteAllSubtasks () {
        allSubtasks.clear();
        for (Epic epic : allEpics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteTaskById (int id) {
        allTasks.remove(id);
    }

    @Override
    public void deleteEpicById (int id) {
        for (Subtask subtask : getEpicSubtasks(id)) {
            allSubtasks.remove(subtask.getTaskId());
        }
        allEpics.remove(id);
    }

    @Override
    public void deleteSubtaskById (int id) {
        for (Epic epic : allEpics.values()) {
            if (epic.getTaskId() == allSubtasks.get(id).getEpicId()) {
                epic.removeSubtask(id);
                updateEpicStatus(allEpics.get(epic.getTaskId()));
            }
        }
        allSubtasks.remove(id);
    }

    @Override
    public Task getTaskById (int id) {
        if (history.getHistory().size() <= 10) {
            history.add(allTasks.get(id));
        } else {
            history.remove(0);
            history.add(allTasks.get(id));
        }
        return allTasks.get(id);
    }

    @Override
    public Epic getEpicById (int id) {
        if (history.getHistory().size() <= 10) {
            history.add(allEpics.get(id));
        } else {
            history.remove(0);
            history.add(allEpics.get(id));
        }
        return allEpics.get(id);
    }

    @Override
    public Subtask getSubtaskById (int id) {
        if (history.getHistory().size() <= 10) {
            history.add(allSubtasks.get(id));
        } else {
            history.remove(0);
            history.add(allSubtasks.get(id));
        }
        return allSubtasks.get(id);
    }

    @Override
    public void updateTask (Task newTask, int id) {
        newTask.setTaskId(id);
        allTasks.put(newTask.getTaskId(), newTask);
    }

    @Override
    public void  updateEpic (Epic newEpic, int id) {
        newEpic.setTaskId(id);
        for (Subtask subtask : allEpics.get(id).getSubtasks().values()) {
            subtask.setThisEpic(newEpic);
            newEpic.addSubtask(subtask);
        }
        allEpics.put(newEpic.getTaskId(), newEpic);
        updateEpicStatus(newEpic);
    }

    @Override
    public void updateSubtask (Subtask newSubtask, int id) {
        newSubtask.setTaskId(id);
        allSubtasks.put(newSubtask.getTaskId(), newSubtask);
        allEpics.get(newSubtask.getEpicId()).addSubtask(newSubtask);
        updateEpicStatus(allEpics.get(newSubtask.getEpicId()));
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        int newStatus = 0;
        int doneStatus = 0;

        for (Subtask subtasks : epic.getSubtasks().values()) {
            if (subtasks.getStatus() == Status.DONE) {
                doneStatus++;
            } else if (subtasks.getStatus() == Status.NEW) {
                newStatus++;
            }
        }

        if (newStatus == epic.getSubtasks().size() || epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else if (doneStatus == epic.getSubtasks().size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}