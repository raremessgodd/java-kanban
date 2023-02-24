package managers;

import tasks.*;

import java.util.ArrayList;

public interface HistoryManager {
    void removeNode(Node node);
    void removeById(int id);
    void linkLast(Task task);
    ArrayList<Task> getTasks();

}
