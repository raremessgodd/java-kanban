import manager.InMemoryTaskManager;
import java.util.Scanner;
import tasks.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task();
        task1.setName("Сходить в зал");
        task1.setStatus("NEW");
        manager.createTask(task1);

        Task task2 = new Task();
        task2.setName("Поиграть на гитаре");
        task2.setStatus("NEW");
        manager.createTask(task2);

        Epic epic = new Epic();
        epic.setName("Выучить язык Java");
        epic.setStatus("NEW");
        manager.createEpic(epic);

        Epic epic2 = new Epic();
        epic2.setName("Завести собаку");
        epic2.setStatus("NEW");
        manager.createEpic(epic2);

        Subtask subtask1 = new Subtask(epic);
        subtask1.setName("Закончить обучение на практикуме");
        subtask1.setStatus("NEW");
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask(epic);
        subtask2.setName("Заниматься самостоятельно");
        subtask2.setStatus("NEW");
        manager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask(epic2);
        subtask3.setName("Накопить денег");
        subtask3.setStatus("NEW");
        manager.createSubtask(subtask3);

        printInformation(manager);

        while (true) {
            System.out.println("\n1 - Удалить задачу по индексу." +
                    "     2 - Изменить статус задачи по индексу.");
            int test = scanner.nextInt();
            if (test == 1) {
                System.out.println("Какой тип задачи вы хотите удалить?");
                String type = scanner.next();
                System.out.println("Введите индекс:");
                int id = scanner.nextInt();
                switch (type) {
                    case ("Task"):
                        manager.deleteTaskById(id);
                        printInformation(manager);
                        break;
                    case ("Epic"):
                        manager.deleteEpicById(id);
                        printInformation(manager);
                        break;
                    case ("Subtask"):
                        manager.deleteSubtaskById(id);
                        printInformation(manager);
                        break;
                    default:
                        System.out.println("Неверный тип задачи.");
                }
            } else if (test == 2) {
                System.out.println("Статус какого типа задачи вы хотите изменить?");
                String type = scanner.next();
                System.out.println("Введите индекс:");
                int id = scanner.nextInt();
                System.out.println("Введите статус:");
                String status = scanner.next();
                switch (type) {
                    case ("Task"):
                        manager.getTaskById(id).setStatus(status);
                        printInformation(manager);
                        break;
                    case ("Epic"):
                        manager.getEpicById(id).setStatus(status);
                        printInformation(manager);
                        break;
                    case ("Subtask"):
                        manager.getSubtaskById(id).setStatus(status);
                        int epicId = manager.getSubtaskById(id).getEpicId();
                        manager.updateEpicStatus(manager.getEpicById(epicId));
                        printInformation(manager);
                        break;
                    default:
                        System.out.println("Неверный тип задачи.");
                }
            } else {
                break;
            }
        }
    }

    public static void printInformation (InMemoryTaskManager InMemoryTaskManager) {
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
