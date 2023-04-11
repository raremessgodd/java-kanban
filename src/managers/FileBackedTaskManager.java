package managers;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static managers.history.InMemoryHistoryManager.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final String path;

    public FileBackedTaskManager(String path) {
        this.path = path;
    }

    public static FileBackedTaskManager loadFromFile (File file) {

        FileBackedTaskManager manager = new FileBackedTaskManager(file.getPath());

        List<String> tasksValues;
        try {
            tasksValues = Files.readAllLines(Paths.get(manager.path));
        } catch (IOException e) {
            throw new ManagerSaveException("Указан неверный путь к файлу: " + e.getMessage());
        }

        for (int i = 1; i < tasksValues.size() - 1; i++) {

            if (tasksValues.get(i).isEmpty()){
                continue;
            }

            Task task = Task.fromString(tasksValues.get(i));

            switch (task.getType()) {
                case TASK:
                    manager.allTasks.put(task.getTaskId(), task);
                    break;
                case SUBTASK:
                    manager.allSubtasks.put(task.getTaskId(), (Subtask) task);
                    break;
                case EPIC:
                    manager.allEpics.put(task.getTaskId(), (Epic) task);
                    break;
            }
        }

        for (Subtask subtask: manager.allSubtasks.values()) {
            Epic epic = manager.allEpics.get(subtask.getEpicId());
            epic.addSubtask(subtask);
            manager.updateEpicStatus(epic);
        }

        List<Integer> splitHistory = historyFromString(tasksValues.get(tasksValues.size() - 1));
        for (int id: splitHistory) {
            if (manager.allTasks.containsKey(id)) {
                manager.history.linkLast(manager.allTasks.get(id));
            } else if (manager.allSubtasks.containsKey(id)) {
                manager.history.linkLast(manager.allSubtasks.get(id));
            } else if (manager.allEpics.containsKey(id)) {
                manager.history.linkLast(manager.allEpics.get(id));
            }
        }

        return manager;
    }

    public void save () throws ManagerSaveException {

        try (Writer writer = Files.newBufferedWriter(Paths.get(path))) {

            writer.write("id,type,name,status,startTime,duration,description,epic," + System.lineSeparator());

            for (Task task : allTasks.values()) {
                writer.write(task.toString() + System.lineSeparator());
            }

            for (Epic epic : allEpics.values()) {
                writer.write(epic.toString() + System.lineSeparator());
            }

            for (Subtask subtask : allSubtasks.values()) {
                writer.write(subtask.toString() + System.lineSeparator());
            }

            writer.write(System.lineSeparator());

            writer.write(historyToString(history));
            if (history.getTasks().isEmpty()) {
                writer.write("0");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Указан неверный путь к файлу: " + path);
        }
    }



    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task newTask, int id) {
        super.updateTask(newTask, id);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic, int id) {
        super.updateEpic(newEpic, id);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask, int id) {
        super.updateSubtask(newSubtask, id);
        save();
    }
}
