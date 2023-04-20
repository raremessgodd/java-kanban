package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        if (subtasks.isEmpty()) {
            setStartTime(subtask.getStartTime());
        }
        subtasks.put(subtask.getTaskId(), subtask);
        updateDuration(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask.getTaskId());
        updateDuration(subtask);
        if (subtasks.isEmpty()) {
            setStartTime(null);
        }
    }

    private void updateDuration(Subtask subtask) {
        Duration duration = getDuration();

        if (subtasks.containsKey(subtask.getTaskId())) {
            duration = duration.plus(subtask.getDuration());
            setDuration(duration);
        } else {
            duration = duration.minus(subtask.getDuration());
            setDuration(duration);
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return getStartTime().plus(getDuration());
    }

    @Override
    public String toString() {
        return super.toString() + subtasks.keySet() + ",";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Epic o = (Epic) obj;
        return o.getTaskId() == this.getTaskId() && o.getName().equals(this.getName()) &&
                o.subtasks.equals(this.subtasks);
    }
}
