package com.vsu.todo.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Реализация хранилища задач в памяти на базе HashMap.
 */
public class InMemoryTaskRepository implements TaskRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryTaskRepository.class);
    private final Map<Long, Task> storage = new HashMap<>();

    @Override
    public Task save(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("task must not be null");
        }
        storage.put(task.getId(), task);
        log.debug("Saved task id={}, title='{}'", task.getId(), task.getTitle());
        return task;
    }

    @Override
    public Optional<Task> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(long id) {
        Task removed = storage.remove(id);
        if (removed != null) {
            log.debug("Deleted task id={}", id);
            return true;
        }
        return false;
    }

    @Override
    public int count() {
        return storage.size();
    }
}
