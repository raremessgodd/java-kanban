package managers;

import tasks.*;
import java.util.LinkedList;

public interface HistoryManager {
    void add(Task task);
    LinkedList<Task> getHistory();

}
