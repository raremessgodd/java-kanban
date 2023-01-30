package tasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(Epic epic) {
        this.epicId = epic.getTaskId();
    }

    public void setThisEpic(Epic thisEpic) {
        this.epicId = thisEpic.getTaskId();
    }

    public int getEpicId() {
        return epicId;
    }

}
