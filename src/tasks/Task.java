package tasks;

import java.time.Duration;
import java.time.ZonedDateTime;

public class Task {
    private String name;
    private int taskId;
    private String description;
    protected Status status;
    private ZonedDateTime startTime;
    private Duration duration;

    public Task() {
        startTime = ZonedDateTime.now();
    }

    public ZonedDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskTypes getType () {
        return TaskTypes.valueOf(this.getClass().getSimpleName().toUpperCase());
    }

    @Override
    public String toString() {
        return (taskId + "," + getType() +
                "," + name + "," + status + "," + description + ",");
    }

    static public Task fromString(String value) {

        String[] values = value.split(",");

        Task task;

        switch (TaskTypes.valueOf(values[1])) {
            case EPIC:
                task = new Epic();
                break;
            case SUBTASK:
                task = new Subtask(Integer.parseInt(values[5]));
                break;
            case TASK:
                task = new Task();
                break;
            default:
                throw new IllegalStateException("ДОДЕЛАТЬ");
        }

        task.setTaskId(Integer.parseInt(values[0]));
        task.setName(values[2]);
        task.setStatus(Status.valueOf(values[3]));
        task.setDescription(values[4]);

        return task;
    }
}
