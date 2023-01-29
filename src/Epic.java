import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void updateStatus() {
        int newStatus = 0;
        int doneStatus = 0;

        for (Subtask subtasks : subtasks.values()) {
            if (subtasks.getStatus().equals("DONE")) {
                doneStatus++;
            } else if (subtasks.getStatus().equals("NEW")) {
                newStatus++;
            }
        }

        if (newStatus == subtasks.size() || subtasks.isEmpty()) {
            this.status = ("NEW");
        } else if (doneStatus == subtasks.size()) {
            this.status = ("DONE");
        } else {
            this.status = ("IN_PROGRESS");
        }
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(HashMap<Integer, Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getTaskId(), subtask);
        updateStatus();
    }

    public void removeSubtask(int id) {
        subtasks.remove(id);
        updateStatus();
    }
}
