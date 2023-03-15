package tasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int epicId) {
        this.epicId = epicId;
    }

    public void setEpicId(Epic thisEpic) {
        this.epicId = thisEpic.getTaskId();
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return super.toString() + epicId + ",";
    }
}
