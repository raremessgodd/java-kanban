package managers;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Epic epic1 = new Epic();
        epic1.setTaskId(1);
        Subtask subtask1 = new Subtask(epic1.getTaskId());
        Subtask subtask2 = new Subtask(epic1.getTaskId());

        manager.createEpic(epic1);

        subtask1.setStartTime(LocalDateTime.now().plusDays(10));
        subtask1.setDuration(Duration.ofHours(50));
        subtask1.setStatus(Status.DONE);
        manager.createSubtask(subtask1);

        subtask2.setStartTime(LocalDateTime.now().plusDays(20));
        subtask2.setDuration(Duration.ofHours(10));
        subtask2.setStatus(Status.IN_PROGRESS);
        manager.createSubtask(subtask2);

        System.out.println(epic1);

    }

    public static void printInformation (TaskManager manager) {

        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Обычные задачи:");
        System.out.println("------------------");
        for (Task task : manager.getAllTasks()) {
            System.out.print("[" + task.getStatus() + "] ");
            System.out.print(task.getName());
            System.out.print(" - " + task.getTaskId());
            System.out.println();
        }

        System.out.println("\nЭпики и их подзадачи:");
        System.out.println("-------------------------");
        for (Epic epic : manager.getAllEpics()) {
            System.out.print("[" + epic.getStatus() + "] ");
            System.out.print(epic.getName());
            System.out.print(" - " + epic.getTaskId());
            System.out.print(" -> " + epic.getSubtasks().keySet());
            System.out.println();
        }

        System.out.println("\nПодзадачи и их эпики:");
        System.out.println("-------------------------");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.print("[" + subtask.getStatus() + "] ");
            System.out.print(subtask.getName());
            System.out.print(" - " + subtask.getTaskId());
            System.out.print(" -> " + subtask.getEpicId());
            System.out.println();
        }

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        System.out.println("История просмотров:");
        for (Task task: manager.getHistory()) {
            System.out.print(task.getTaskId() + " -> ");
        }

        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
    }
}
