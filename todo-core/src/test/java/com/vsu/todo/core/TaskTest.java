package com.vsu.todo.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Task model")
class TaskTest {

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task(1L, "Прочитать книгу", "Эффективная Java", Priority.MEDIUM);
    }

    @Test
    @DisplayName("конструктор корректно инициализирует поля")
    void constructorInitializesFields() {
        assertThat(task.getId()).isEqualTo(1L);
        assertThat(task.getTitle()).isEqualTo("Прочитать книгу");
        assertThat(task.getDescription()).isEqualTo("Эффективная Java");
        assertThat(task.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.NEW);
        assertThat(task.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("null description заменяется на пустую строку")
    void nullDescriptionBecomesEmpty() {
        Task t = new Task(2L, "X", null, Priority.LOW);
        assertThat(t.getDescription()).isEmpty();
    }

    @Test
    @DisplayName("null priority заменяется на MEDIUM")
    void nullPriorityBecomesMedium() {
        Task t = new Task(2L, "X", "y", null);
        assertThat(t.getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    @DisplayName("отрицательный id запрещён")
    void negativeIdRejected() {
        assertThatThrownBy(() -> new Task(-1, "x", "y", Priority.LOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("пустой title запрещён")
    void blankTitleRejected() {
        assertThatThrownBy(() -> new Task(1, "   ", "y", Priority.LOW))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Task(1, null, "y", Priority.LOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("сеттеры обновляют поля и проверяют входные данные")
    void settersWork() {
        task.setTitle("Новый заголовок");
        task.setDescription("Другое описание");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPriority(Priority.HIGH);

        assertThat(task.getTitle()).isEqualTo("Новый заголовок");
        assertThat(task.getDescription()).isEqualTo("Другое описание");
        assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(task.getPriority()).isEqualTo(Priority.HIGH);

        assertThatThrownBy(() -> task.setTitle(""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> task.setStatus(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("setDescription(null) → пустая строка")
    void setDescriptionNullBecomesEmpty() {
        task.setDescription(null);
        assertThat(task.getDescription()).isEmpty();
    }

    @Test
    @DisplayName("isDone отражает статус DONE")
    void isDoneReflectsStatus() {
        assertThat(task.isDone()).isFalse();
        task.setStatus(TaskStatus.DONE);
        assertThat(task.isDone()).isTrue();
    }

    @Test
    @DisplayName("equals и hashCode основаны только на id")
    void equalsAndHashCodeByIdOnly() {
        Task other = new Task(1L, "Другой", "Другое", Priority.HIGH);
        assertThat(task).isEqualTo(other);
        assertThat(task.hashCode()).isEqualTo(other.hashCode());

        Task different = new Task(2L, "Прочитать книгу", "Эффективная Java", Priority.MEDIUM);
        assertThat(task).isNotEqualTo(different);
        assertThat(task).isNotEqualTo(null);
        assertThat(task).isNotEqualTo("string");
    }

    @Test
    @DisplayName("toString содержит ключевые поля")
    void toStringContainsKeyFields() {
        String s = task.toString();
        assertThat(s).contains("id=1", "Прочитать книгу", "NEW", "MEDIUM");
    }
}
