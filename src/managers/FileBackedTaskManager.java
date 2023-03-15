package managers;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final String path;

    public FileBackedTaskManager(String path) {
        this.path = path;
    }

    public void save () {

        try (Writer writer = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8, true))) {

            for (Task task : getAllTasks()) {
                writer.write(task.toString() + "\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(subtask.toString() + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(epic.toString() + "\n");
            }

            writer.write("\n");

            for (Task task : getHistory()) {
                writer.write(task.getTaskId() + ",");
            }

        } catch (IOException e) {
            System.out.println("ДОДЕЛАТЬ");
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
