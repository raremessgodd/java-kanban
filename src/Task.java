public class Task {
    private String name;
    private int taskId;
    private String description;
    protected String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
