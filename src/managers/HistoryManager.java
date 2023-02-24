package managers;

import tasks.*;

import java.util.ArrayList;

public interface HistoryManager {
    void removeNode(Node node);
    void linkLast(Task task);
    ArrayList<Task> getTasks();

}
