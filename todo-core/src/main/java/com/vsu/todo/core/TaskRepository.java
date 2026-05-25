package com.vsu.todo.core;

import java.util.List;
import java.util.Optional;

/**
 * Хранилище задач. Реализации могут использовать память, файл или БД.
 * Интерфейс позволяет подменять реализацию моком в тестах сервисов.
 */
public interface TaskRepository {

    /** Сохранить новую задачу или обновить существующую. */
    Task save(Task task);

    /** Найти задачу по id. */
    Optional<Task> findById(long id);

    /** Получить все задачи. */
    List<Task> findAll();

    /** Удалить задачу. Возвращает true, если задача существовала. */
    boolean deleteById(long id);

    /** Количество задач в хранилище. */
    int count();
}
