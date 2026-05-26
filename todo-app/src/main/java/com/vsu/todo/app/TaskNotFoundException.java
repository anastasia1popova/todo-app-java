package com.vsu.todo.app;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(long id) {
        super("Task with id=" + id + " not found");
    }
}
