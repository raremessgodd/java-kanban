package managers;

import java.util.*;

import exceptions.EqualTimeException;
import managers.history.HistoryManager;
import tasks.*;

public class InMemoryTaskManager implements TaskManager {
    private int id = 1;

    protected final HistoryManager history = Managers.getDefaultHistory();
    protected final HashMap<Integer, Task> allTasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> allSubtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> allEpics = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public int setId () {
        return id++;
    }

    public Set<Task> getPrioritizedTasks() {
        prioritizedTasks.addAll(allTasks.values());
        prioritizedTasks.addAll(allSubtasks.values());
        return prioritizedTasks;
    }

    public void checkIntersection(Task other) throws EqualTimeException {
        for (Task task: getPrioritizedTasks()) {
            if (!other.getStartTime().isAfter(task.getEndTime()) &&
                    !other.getEndTime().isBefore(task.getStartTime())) {
                throw new EqualTimeException("Две задачи пересекаются по времени: id." +
                        task.getTaskId() + " - id." + other.getTaskId());
            }
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(allTasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(allSubtasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(allEpics.values());
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        return new ArrayList<>(allEpics.get(epicId).getSubtasks().values());
    }

    @Override
    public void createTask (Task task) {
        task.setTaskId(setId());
        checkIntersection(task);
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
        checkIntersection(subtask);
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
        history.removeById(id);
        allTasks.remove(id);
    }

    @Override
    public void deleteEpicById (int id) {
        for (Subtask subtask : getEpicSubtasks(id)) {
            history.removeById(subtask.getTaskId());
            allSubtasks.remove(subtask.getTaskId());
        }
        history.removeById(id);
        allEpics.remove(id);
    }

    @Override
    public void deleteSubtaskById (int id) {
        Subtask subtask = allSubtasks.get(id);
        allEpics.get(subtask.getEpicId()).removeSubtask(subtask);
        updateEpicStatus(allEpics.get(subtask.getEpicId()));
        history.removeById(id);
        allSubtasks.remove(id);
    }

    @Override
    public Task getTaskById (int id) {
        history.linkLast(allTasks.get(id));
        return allTasks.get(id);
    }

    @Override
    public Epic getEpicById (int id) {
        history.linkLast(allEpics.get(id));
        for (Subtask subtask : allEpics.get(id).getSubtasks().values()) {
            history.linkLast(subtask);
        }
        return allEpics.get(id);
    }

    @Override
    public Subtask getSubtaskById (int id) {
        history.linkLast(allEpics.get(allSubtasks.get(id).getEpicId()));
        history.linkLast(allSubtasks.get(id));
        return allSubtasks.get(id);
    }

    @Override
    public void updateTask (Task newTask, int id) {
        newTask.setTaskId(id);
        checkIntersection(newTask);
        allTasks.put(newTask.getTaskId(), newTask);
    }

    @Override
    public void updateEpic (Epic newEpic, int id) {
        newEpic.setTaskId(id);
        for (Subtask subtask : allEpics.get(id).getSubtasks().values()) {
            subtask.setEpicId(newEpic.getTaskId());
            newEpic.addSubtask(subtask);
        }
        allEpics.put(newEpic.getTaskId(), newEpic);
        updateEpicStatus(newEpic);
    }

    @Override
    public void updateSubtask (Subtask newSubtask, int id) {
        newSubtask.setTaskId(id);
        checkIntersection(newSubtask);
        allEpics.get(newSubtask.getEpicId()).removeSubtask(allSubtasks.get(id));
        allEpics.get(newSubtask.getEpicId()).addSubtask(newSubtask);
        allSubtasks.put(newSubtask.getTaskId(), newSubtask);
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
