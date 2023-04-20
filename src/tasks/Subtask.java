package tasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int epicId) {
        this.epicId = epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return super.toString() + epicId + ",";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Subtask o = (Subtask) obj;
        return super.equals(obj) && o.getEpicId() == this.getEpicId();
    }
}
