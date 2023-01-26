public class Subtask extends Task{
    int epicId;

    public Subtask(Epic epic) {
        this.epicId = epic.taskId;
    }
}
