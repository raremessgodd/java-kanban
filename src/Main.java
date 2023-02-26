import managers.Managers;
import java.util.Scanner;

import managers.TaskManager;
import tasks.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task();
        task1.setName("Сходить в зал");
        task1.setStatus(Status.NEW);
        manager.createTask(task1);

        Task task2 = new Task();
        task2.setName("Поиграть на гитаре");
        task2.setStatus(Status.NEW);
        manager.createTask(task2);

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
        subtask3.setName("Закончить ВУЗ");
        subtask3.setStatus(Status.NEW);
        manager.createSubtask(subtask3);

        printInformation(manager);

        while (true) {
            System.out.println("\n1 - Просмотреть задачу по ID." +
                    "     2 - Удалить задачу по ID." +
                    "     3 - Посмотреть историю просмотров.");
            int test = scanner.nextInt();

            if (test == 1) {
                System.out.println("Какой тип задачи вы хотите просмотреть?");
                String type = scanner.next();
                System.out.println("Введите индекс:");
                int id = scanner.nextInt();

                switch (type) {
                    case ("Task"):
                        manager.getTaskById(id);
                        System.out.println("Задача №" + id + " просмотрена.");
                        break;
                    case ("Epic"):
                        manager.getEpicById(id);
                        System.out.println("Эпип №" + id + " просмотрен.");
                        break;
                    case ("Subtask"):
                        manager.getSubtaskById(id);
                        System.out.println("Подзадача №" + id + " просмотрена.");
                        break;
                    default:
                        System.out.println("Неверный тип задачи.");
                }
            }
            else if (test == 2) {
                System.out.println("Введите индекс:");
                int id = scanner.nextInt();

                manager.deleteEpicById(id);
                printInformation(manager);
            }
            else if (test == 3) {
                for (Task task : manager.getHistory()) {
                    System.out.println(task.getTaskId() + ". " + task.getName());
                }
            }
            else {
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
