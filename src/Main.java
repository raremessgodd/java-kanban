import managers.Managers;
import java.util.Scanner;

import managers.TaskManager;
import tasks.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager manager = Managers.getDefault();

        Epic epic1 = new Epic();
        epic1.setName("Выучить язык Java");
        epic1.setStatus(Status.NEW);
        manager.createEpic(epic1);

        Epic epic2 = new Epic();
        epic2.setName("Завести собаку");
        epic2.setStatus(Status.NEW);
        manager.createEpic(epic2);

        Subtask subtask1 = new Subtask(epic1);
        subtask1.setName("Закончить обучение на практикуме");
        subtask1.setStatus(Status.NEW);
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask(epic1);
        subtask2.setName("Заниматься самостоятельно");
        subtask2.setStatus(Status.NEW);
        manager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask(epic1);
        subtask3.setName("Накопить денег");
        subtask3.setStatus(Status.NEW);
        manager.createSubtask(subtask3);

        printInformation(manager);

        while (true) {
            System.out.println("\n1 - Просмотреть epic1." +
                    "     2 - Просмотреть epic1." +
                    "     3 - Удалить задачу по ID." +
                    "     4 - Посмотреть историю просмотров.");
            int test = scanner.nextInt();
            if (test == 1) {
                manager.getEpicById(1);
                System.out.println("Epic1 просмотрен.");
            } else if (test == 2) {
                manager.getEpicById(2);
                System.out.println("Epic2 просмотрен.");
            } else if (test == 3) {
                System.out.println("Введите индекс:");
                int id = scanner.nextInt();
                manager.deleteEpicById(id);
                printInformation(manager);
            } else if (test == 4) {
                System.out.println(manager.getHistory().toString());
            } else {
                break;
            }
        }
    }

    public static void printInformation (TaskManager InMemoryTaskManager) {
        System.out.println("\nОбычные задачи:");
        System.out.println("------------------");
        for (Task task : InMemoryTaskManager.getAllTasks()) {
            System.out.print("[" + task.getStatus() + "] ");
            System.out.print(task.getName());
            System.out.print(" - " + task.getTaskId());
            System.out.println();
        }

        System.out.println("\nЭпики и их подзадачи:");
        System.out.println("-------------------------");
        for (Epic epic : InMemoryTaskManager.getAllEpics()) {
            System.out.print("[" + epic.getStatus() + "] ");
            System.out.print(epic.getName());
            System.out.print(" - " + epic.getTaskId());
            System.out.print(" -> " + epic.getSubtasks().keySet());
            System.out.println();
        }

        System.out.println("\nПодзадачи и их эпики:");
        System.out.println("-------------------------");
        for (Subtask subtask : InMemoryTaskManager.getAllSubtasks()) {
            System.out.print("[" + subtask.getStatus() + "] ");
            System.out.print(subtask.getName());
            System.out.print(" - " + subtask.getTaskId());
            System.out.print(" -> " + subtask.getEpicId());
            System.out.println();
        }
    }
}
