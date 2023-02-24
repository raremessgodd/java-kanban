package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void linkLast(Task task) {
        final Node oldNode = tail;
        final Node newNode = new Node(oldNode, task, null);
        tail = newNode;

        if (history.containsKey(task.getTaskId())) {
            removeNode(history.get(task.getTaskId()));
        }

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
            node.next = null;
        }

        if (nextNode == null) {
            tail = prevNode;
        } else {
            nextNode.prev = prevNode;
            node.prev = null;
        }

        history.remove(node.task.getTaskId());
        node.task = null;
    }

    @Override
    public ArrayList<Task> getTasks(){
        ArrayList<Task> tasks = new ArrayList<>();
        Node node = head;

        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }

        return tasks;
    }

}
