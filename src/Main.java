import managers.Managers;

import managers.TaskManager;
import tasks.*;

public class Main {

    public static void main(String[] args) {
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

        Subtask subtask1 = new Subtask(epic1.getTaskId());
        subtask1.setName("Закончить обучение на практикуме");
        subtask1.setStatus(Status.NEW);
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask(epic1.getTaskId());
        subtask2.setName("Заниматься самостоятельно");
        subtask2.setStatus(Status.NEW);
        manager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask(epic1.getTaskId());
        subtask3.setName("Закончить ВУЗ");
        subtask3.setStatus(Status.NEW);
        manager.createSubtask(subtask3);

        printInformation(manager);

        manager.getEpicById(epic2.getTaskId());
        manager.getTaskById(task1.getTaskId());
        manager.getEpicById(epic1.getTaskId());
        manager.getTaskById(task2.getTaskId());
        manager.getTaskById(task1.getTaskId());
        manager.getEpicById(epic2.getTaskId());
        manager.getEpicById(epic2.getTaskId());
        manager.getEpicById(epic1.getTaskId());

        System.out.println("\nПервый тест: ");
        for (Task task: manager.getHistory()) {
            System.out.println(task.getTaskId() + ". " + task.getName());
        }

        manager.deleteEpicById(epic1.getTaskId());

        System.out.println("\nВторой тест: ");
        for (Task task: manager.getHistory()) {
            System.out.println(task.getTaskId() + ". " + task.getName());
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
