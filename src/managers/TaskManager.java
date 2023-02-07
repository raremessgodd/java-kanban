package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;

public interface TaskManager {

    LinkedList<Task> getHistory();

    ArrayList<Task> getAllTasks();
    ArrayList<Subtask> getAllSubtasks();
    ArrayList<Epic> getAllEpics();
    ArrayList<Subtask> getEpicSubtasks(int epicId);

    void createTask (Task task);
    void createEpic (Epic epic);
    void createSubtask (Subtask subtask);

    void deleteAllTasks ();
    void deleteAllEpics ();
    void deleteAllSubtasks ();

    void deleteTaskById (int id);
    void deleteEpicById (int id);
    void deleteSubtaskById (int id);

    Task getTaskById (int id);
    Epic getEpicById (int id);
    Subtask getSubtaskById (int id);

    void updateTask (Task newTask, int id);
    void  updateEpic (Epic newEpic, int id);
    void updateSubtask (Subtask newSubtask, int id);

    void updateEpicStatus(Epic epic);

}
