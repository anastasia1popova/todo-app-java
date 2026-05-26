package com.vsu.todo.app;

import com.vsu.todo.core.InMemoryTaskRepository;
import com.vsu.todo.core.Priority;
import com.vsu.todo.core.Task;
import com.vsu.todo.core.TaskRepository;
import com.vsu.todo.core.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Простой консольный интерфейс TODO-приложения.
 * Поддерживает добавление, просмотр, изменение статуса и удаление задач.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Запуск TODO-приложения");
        TaskRepository repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);

        // демо-данные
        service.createTask("Изучить Git", "Базовые команды и git-flow", Priority.HIGH);
        service.createTask("Настроить Maven", "Создать pom.xml и сборку", Priority.MEDIUM);
        service.createTask("Написать тесты", "JUnit + Mockito + Jacoco", Priority.HIGH);

        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true;
            printMenu();
            while (running) {
                System.out.print("> ");
                if (!sc.hasNextLine()) break;
                String cmd = sc.nextLine().trim();
                try {
                    running = dispatch(cmd, sc, service);
                } catch (Exception ex) {
                    System.out.println("Ошибка: " + ex.getMessage());
                    log.error("Команда '{}' завершилась с ошибкой", cmd, ex);
                }
            }
        }
        System.out.println("Выход. Всего хорошего!");
    }

    private static boolean dispatch(String cmd, Scanner sc, TaskService service) {
        switch (cmd) {
            case "1": addTask(sc, service); return true;
            case "2": listTasks(service); return true;
            case "3": changeStatus(sc, service); return true;
            case "4": deleteTask(sc, service); return true;
            case "5": showStats(service); return true;
            case "h": printMenu(); return true;
            case "q": return false;
            default:
                System.out.println("Неизвестная команда. Введите h для помощи.");
                return true;
        }
    }

    private static void printMenu() {
        System.out.println("""
                === TODO ===
                1 — добавить задачу
                2 — показать все задачи
                3 — изменить статус
                4 — удалить задачу
                5 — статистика выполнения
                h — справка
                q — выход""");
    }

    private static void addTask(Scanner sc, TaskService service) {
        System.out.print("Заголовок: ");
        String title = sc.nextLine();
        System.out.print("Описание: ");
        String desc = sc.nextLine();
        System.out.print("Приоритет (LOW/MEDIUM/HIGH/CRITICAL): ");
        Priority p = Priority.valueOf(sc.nextLine().trim().toUpperCase());
        Task t = service.createTask(title, desc, p);
        System.out.println("Создана: " + t);
    }

    private static void listTasks(TaskService service) {
        List<Task> tasks = service.listAll();
        if (tasks.isEmpty()) {
            System.out.println("(пусто)");
            return;
        }
        tasks.forEach(System.out::println);
    }

    private static void changeStatus(Scanner sc, TaskService service) {
        System.out.print("ID задачи: ");
        long id = Long.parseLong(sc.nextLine().trim());
        System.out.print("Новый статус (NEW/IN_PROGRESS/DONE/CANCELLED): ");
        TaskStatus st = TaskStatus.valueOf(sc.nextLine().trim().toUpperCase());
        Task t = service.updateStatus(id, st);
        System.out.println("Обновлено: " + t);
    }

    private static void deleteTask(Scanner sc, TaskService service) {
        System.out.print("ID задачи: ");
        long id = Long.parseLong(sc.nextLine().trim());
        boolean ok = service.deleteTask(id);
        System.out.println(ok ? "Удалено" : "Задача не найдена");
    }

    private static void showStats(TaskService service) {
        double ratio = service.completionRatio();
        System.out.printf("Выполнено: %.1f%% (всего задач: %d)%n",
                ratio * 100.0, service.listAll().size());
    }
}
