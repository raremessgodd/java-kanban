package managers;

import tasks.Task;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (history.size() < 10) {
            history.addLast(task);
        } else {
            history.removeFirst();
            history.addLast(task);
        }
    }

    @Override
    public LinkedList<Task> getHistory(){
        return history;
    }
}
