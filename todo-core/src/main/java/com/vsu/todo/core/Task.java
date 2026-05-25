package com.vsu.todo.core;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Модель задачи. Неизменяемые поля: id, createdAt.
 * Изменяемые: title, description, status, priority.
 */
public class Task {

    private final long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private final LocalDateTime createdAt;

    public Task(long id, String title, String description, Priority priority) {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be positive");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        this.id = id;
        this.title = title;
        this.description = description == null ? "" : description;
        this.priority = priority == null ? Priority.MEDIUM : priority;
        this.status = TaskStatus.NEW;
        this.createdAt = LocalDateTime.now();
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public Priority getPriority() { return priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public void setStatus(TaskStatus status) {
        this.status = Objects.requireNonNull(status, "status");
    }

    public void setPriority(Priority priority) {
        this.priority = Objects.requireNonNull(priority, "priority");
    }

    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("Task{id=%d, title='%s', status=%s, priority=%s}",
                id, title, status, priority);
    }
}
