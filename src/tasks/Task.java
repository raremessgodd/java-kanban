package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private String name;
    private int taskId;
    protected Status status;
    private LocalDateTime startTime;
    private Duration duration = Duration.ZERO;
    private String description;

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
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
        return (taskId + "," + getType() + "," + name + "," + status + ","
                + startTime + "," + duration + "," + description + ",");
    }

    static public Task fromString(String value) {

        String[] values = value.split(",");

        Task task;

        switch (TaskTypes.valueOf(values[1])) {
            case EPIC:
                task = new Epic();
                break;
            case SUBTASK:
                task = new Subtask(Integer.parseInt(values[7]));
                break;
            case TASK:
                task = new Task();
                break;
            default:
                throw new IllegalStateException();
        }

        try {
            task.setTaskId(Integer.parseInt(values[0]));
            task.setName(values[2]);
            task.setStatus(Status.valueOf(values[3]));
            task.setDescription(values[6]);
            if(task.getType() != TaskTypes.EPIC) {
                task.setStartTime(LocalDateTime.parse(values[4]));
                task.setDuration(Duration.parse(values[5]));
            }
        } catch (Exception e) {
            System.out.println("Ошибка преобразования: " + e.getMessage());
        }
        return task;
    }
}
