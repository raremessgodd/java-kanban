package managers.history;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void linkLast(Task task) {

        if (history.containsKey(task.getTaskId())) {
            removeNode(history.get(task.getTaskId()));
        }

        final Node oldNode = tail;
        final Node newNode = new Node(oldNode, task, null);
        tail = newNode;

        if (oldNode == null){
            head = newNode;
        } else {
            oldNode.next = newNode;
        }

        history.put(task.getTaskId(), newNode);
    }

    @Override
    public void removeNode(Node node) {

        final Node prevNode = node.prev;
        final Node nextNode = node.next;

        if (prevNode == null) {
            head = nextNode;
        } else {
            prevNode.next = nextNode;
            node.prev = null;
        }

        if (nextNode == null) {
            tail = prevNode;
        } else {
            nextNode.prev = prevNode;
            node.next = null;
        }

        history.remove(node.task.getTaskId());
        node.task = null;
    }

    @Override
    public void removeById(int id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
        }
    }

    @Override
    public ArrayList<Task> getTasks(){
        final ArrayList<Task> tasks = new ArrayList<>();
        Node node = head;

        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }

        return tasks;
    }

    public static String historyToString (HistoryManager manager) {
        StringBuilder values = new StringBuilder();

        for (Task task : manager.getTasks()) {
            values.append(task.getTaskId()).append(",");
        }

        return values.toString();
    }

    public static List<Integer> historyFromString (String value) {
        String[] values = value.split(",");
        List<Integer> history = new ArrayList<>();

        for (String id : values) {
            history.add(Integer.parseInt(id));
        }

        return history;
    }

}
