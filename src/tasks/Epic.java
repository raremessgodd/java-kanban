package tasks;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        if (subtasks.isEmpty()) {
            this.setStartTime(subtask.getStartTime());
        }
        subtasks.put(subtask.getTaskId(), subtask);
        updateDuration();
    }

    public void removeSubtask(int id) {
        subtasks.remove(id);
    }

    private void updateDuration() {
        for (Subtask subtask: subtasks.values()) {
            Duration duration = this.getDuration();
            duration = duration.plus(subtask.getDuration());
            this.setDuration(duration);
        }
    }

    @Override
    public ZonedDateTime getEndTime() {
        ZonedDateTime endTime = this.getStartTime();
        for (Subtask subtask : subtasks.values()) {
            endTime = endTime.plus(subtask.getDuration());
        }
        return endTime;
    }

    @Override
    public String toString() {
        return super.toString() + subtasks.keySet() + ",";
    }
}
