package com.vsu.todo.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("InMemoryTaskRepository")
class InMemoryTaskRepositoryTest {

    private InMemoryTaskRepository repo;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        repo = new InMemoryTaskRepository();
        task1 = new Task(1L, "first", "d1", Priority.LOW);
        task2 = new Task(2L, "second", "d2", Priority.HIGH);
    }

    @Test
    @DisplayName("save сохраняет задачу, findById её возвращает")
    void saveAndFind() {
        repo.save(task1);
        Optional<Task> found = repo.findById(1L);
        assertThat(found).isPresent().contains(task1);
    }

    @Test
    @DisplayName("findById возвращает empty для несуществующего id")
    void findMissingReturnsEmpty() {
        assertThat(repo.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("save(null) бросает исключение")
    void saveNullThrows() {
        assertThatThrownBy(() -> repo.save(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("findAll возвращает все задачи")
    void findAllReturnsEverything() {
        repo.save(task1);
        repo.save(task2);
        assertThat(repo.findAll()).containsExactlyInAnyOrder(task1, task2);
    }

    @Test
    @DisplayName("deleteById возвращает true для существующего, false иначе")
    void deleteWorks() {
        repo.save(task1);
        assertThat(repo.deleteById(1L)).isTrue();
        assertThat(repo.deleteById(1L)).isFalse();
        assertThat(repo.findAll()).isEmpty();
    }

    @Test
    @DisplayName("count корректно отражает размер")
    void countReflectsSize() {
        assertThat(repo.count()).isZero();
        repo.save(task1);
        repo.save(task2);
        assertThat(repo.count()).isEqualTo(2);
        repo.deleteById(1L);
        assertThat(repo.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("повторный save с тем же id перезаписывает запись")
    void saveOverwritesExisting() {
        repo.save(task1);
        Task updated = new Task(1L, "updated", "", Priority.CRITICAL);
        repo.save(updated);
        assertThat(repo.findById(1L)).contains(updated);
        assertThat(repo.count()).isEqualTo(1);
    }
}
