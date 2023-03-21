package managers;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static managers.history.InMemoryHistoryManager.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public static void main(String[] args) {
        FileBackedTaskManager manager1 = new FileBackedTaskManager("data/tasks_data.csv");

        Task task1 = new Task();
        task1.setName("Сходить в зал");
        task1.setStatus(Status.NEW);
        manager1.createTask(task1);

        Task task2 = new Task();
        task2.setName("Поиграть на гитаре");
        task2.setStatus(Status.NEW);
        manager1.createTask(task2);

        Epic epic1 = new Epic();
        epic1.setName("Выучить язык Java");
        epic1.setStatus(Status.NEW);
        manager1.createEpic(epic1);

        Epic epic2 = new Epic();
        epic2.setName("Завести собаку");
        epic2.setStatus(Status.NEW);
        manager1.createEpic(epic2);

        Subtask subtask1 = new Subtask(epic1.getTaskId());
        subtask1.setName("Закончить обучение на практикуме");
        subtask1.setStatus(Status.NEW);
        manager1.createSubtask(subtask1);

        Subtask subtask2 = new Subtask(epic1.getTaskId());
        subtask2.setName("Заниматься самостоятельно");
        subtask2.setStatus(Status.NEW);
        manager1.createSubtask(subtask2);

        Subtask subtask3 = new Subtask(epic1.getTaskId());
        subtask3.setName("Закончить ВУЗ");
        subtask3.setStatus(Status.NEW);
        manager1.createSubtask(subtask3);

        System.out.println("\nТест №1:");
        manager1.getEpicById(3);
        Main.printInformation(manager1);

        TaskManager manager2 = FileBackedTaskManager.loadFromFile(Paths.get("data/tasks_data.csv").toFile());

        System.out.println("Тест 2:");
        Main.printInformation(manager2);
    }

    private final String path;

    public static FileBackedTaskManager loadFromFile (File file) throws ManagerSaveException {

        FileBackedTaskManager manager = new FileBackedTaskManager(file.getPath());

        String[] tasksValues;
        try {
            tasksValues = Files.readString(Paths.get(manager.path)).split("\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Неверный путь к файлу.");
        }

        for (int i = 1; i < tasksValues.length - 2; i++) {

            Task task = Task.fromString(tasksValues[i]);

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
            manager.allEpics.get(subtask.getEpicId()).addSubtask(subtask);
        }

        List<Integer> splitHistory = historyFromString(tasksValues[tasksValues.length - 1]);
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

    public FileBackedTaskManager(String path) {
        this.path = path;
    }

    public void save () throws ManagerSaveException {

        try (Writer writer = Files.newBufferedWriter(Paths.get(path))) {

            writer.write("id,type,name,status,description,epic," + "\n");

            for (Task task : allTasks.values()) {
                writer.write(task.toString() + "\n");
            }

            for (Epic epic : allEpics.values()) {
                writer.write(epic.toString() + "\n");
            }

            for (Subtask subtask : allSubtasks.values()) {
                writer.write(subtask.toString() + "\n");
            }

            writer.write("\n");

            writer.write(historyToString(history));
            if (history.getTasks().isEmpty()) {
                writer.write("0");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Указан неверный путь к файлу.");
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
