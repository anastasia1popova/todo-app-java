package com.vsu.todo.app;

import com.vsu.todo.core.Priority;
import com.vsu.todo.core.Task;
import com.vsu.todo.core.TaskRepository;
import com.vsu.todo.core.TaskStatus;
import com.vsu.todo.core.TaskValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Основная бизнес-логика управления задачами. Использует TaskRepository
 * (внедряется через конструктор), что позволяет тестировать сервис
 * изолированно с помощью Mockito.
 */
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository repository;
    private final AtomicLong idGenerator = new AtomicLong(0);

    public TaskService(TaskRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
        // если в репозитории уже есть данные, продолжаем нумерацию
        repository.findAll().stream()
                .mapToLong(Task::getId)
                .max()
                .ifPresent(idGenerator::set);
    }

    public Task createTask(String title, String description, Priority priority) {
        TaskValidator.validateTitle(title);
        TaskValidator.validateDescription(description);
        long id = idGenerator.incrementAndGet();
        Task task = new Task(id, title, description, priority);
        repository.save(task);
        log.info("Created task id={} title='{}'", id, title);
        return task;
    }

    public Task updateStatus(long id, TaskStatus newStatus) {
        Objects.requireNonNull(newStatus, "newStatus");
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.setStatus(newStatus);
        repository.save(task);
        log.info("Updated task id={} status -> {}", id, newStatus);
        return task;
    }

    public Task markDone(long id) {
        return updateStatus(id, TaskStatus.DONE);
    }

    public boolean deleteTask(long id) {
        boolean deleted = repository.deleteById(id);
        if (deleted) {
            log.info("Deleted task id={}", id);
        } else {
            log.warn("Attempt to delete missing task id={}", id);
        }
        return deleted;
    }

    public List<Task> listAll() {
        return repository.findAll();
    }

    public List<Task> listByStatus(TaskStatus status) {
        Objects.requireNonNull(status, "status");
        return repository.findAll().stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Task> listByPriority(Priority priority) {
        Objects.requireNonNull(priority, "priority");
        return repository.findAll().stream()
                .filter(t -> t.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public Task findTask(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    /**
     * Доля выполненных задач от общего числа. Если задач нет, возвращает 0.
     */
    public double completionRatio() {
        List<Task> all = repository.findAll();
        if (all.isEmpty()) {
            return 0.0;
        }
        long done = all.stream().filter(Task::isDone).count();
        return (double) done / all.size();
    }
}
