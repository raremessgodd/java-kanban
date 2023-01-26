import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void setStatus() {
        int newStatus = 0;
        int doneStatus = 0;

        for (Subtask subtasks : subtasks.values()) {
            if (subtasks.status.equals("DONE")) {
                doneStatus++;
            } else if (subtasks.status.equals("NEW")) {
                newStatus++;
            }
        }

        if (newStatus == subtasks.size()) {
            this.status = "NEW";
        } else if (doneStatus == subtasks.size()) {
            this.status = "DONE";
        } else {
            this.status = "IN_PROGRESS";
        }
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.taskId, subtask);
    }
}
