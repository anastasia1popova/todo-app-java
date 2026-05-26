package com.vsu.todo.app;

import com.vsu.todo.core.Priority;
import com.vsu.todo.core.Task;
import com.vsu.todo.core.TaskRepository;
import com.vsu.todo.core.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты сервисного слоя с подменой репозитория мок-объектом.
 * Демонстрирует возможности Mockito: when().thenReturn(), verify(), ArgumentCaptor.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService (с замоканным репозиторием)")
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    private TaskService service;

    @BeforeEach
    void setUp() {
        // Стаб для конструктора: при инициализации сервис вызывает findAll()
        when(repository.findAll()).thenReturn(Collections.emptyList());
        service = new TaskService(repository);
    }

    @Test
    @DisplayName("createTask: сервис вызывает repository.save с правильной задачей")
    void createTaskSavesViaRepository() {
        Task created = service.createTask("Купить молоко", "Утром", Priority.LOW);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(repository).save(captor.capture());

        Task saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo("Купить молоко");
        assertThat(saved.getDescription()).isEqualTo("Утром");
        assertThat(saved.getPriority()).isEqualTo(Priority.LOW);
        assertThat(saved.getStatus()).isEqualTo(TaskStatus.NEW);
        assertThat(created).isSameAs(saved);
    }

    @Test
    @DisplayName("createTask: id-генератор увеличивается между вызовами")
    void idGeneratorIncreases() {
        Task t1 = service.createTask("a", "", Priority.LOW);
        Task t2 = service.createTask("b", "", Priority.LOW);
        Task t3 = service.createTask("c", "", Priority.LOW);

        assertThat(t1.getId()).isEqualTo(1L);
        assertThat(t2.getId()).isEqualTo(2L);
        assertThat(t3.getId()).isEqualTo(3L);
        verify(repository, times(3)).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask с пустым заголовком — IllegalArgumentException, save не вызывается")
    void createTaskRejectsBlankTitle() {
        assertThatThrownBy(() -> service.createTask("  ", "x", Priority.LOW))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("updateStatus: меняет статус и сохраняет")
    void updateStatusChangesAndSaves() {
        Task existing = new Task(42L, "test", "", Priority.MEDIUM);
        when(repository.findById(42L)).thenReturn(Optional.of(existing));

        Task result = service.updateStatus(42L, TaskStatus.IN_PROGRESS);

        assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("updateStatus: задача не найдена → TaskNotFoundException")
    void updateStatusMissingTaskThrows() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(999L, TaskStatus.DONE))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");

        verify(repository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("markDone: ставит статус DONE")
    void markDoneSetsDoneStatus() {
        Task existing = new Task(1L, "x", "", Priority.LOW);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        Task result = service.markDone(1L);

        assertThat(result.isDone()).isTrue();
        assertThat(result.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    @DisplayName("deleteTask: делегирует репозиторию и пробрасывает результат")
    void deleteTaskDelegates() {
        when(repository.deleteById(7L)).thenReturn(true);
        assertThat(service.deleteTask(7L)).isTrue();
        verify(repository).deleteById(7L);

        when(repository.deleteById(8L)).thenReturn(false);
        assertThat(service.deleteTask(8L)).isFalse();
    }

    @Test
    @DisplayName("findTask: пробрасывает результат репозитория")
    void findTaskReturnsExisting() {
        Task t = new Task(5L, "найти", "", Priority.LOW);
        when(repository.findById(5L)).thenReturn(Optional.of(t));
        assertThat(service.findTask(5L)).isSameAs(t);
    }

    @Test
    @DisplayName("findTask: отсутствует → TaskNotFoundException")
    void findTaskMissingThrows() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findTask(100L))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    @DisplayName("listAll: возвращает результат репозитория")
    void listAllDelegates() {
        Task t1 = new Task(1L, "a", "", Priority.LOW);
        Task t2 = new Task(2L, "b", "", Priority.HIGH);
        when(repository.findAll()).thenReturn(Arrays.asList(t1, t2));
        assertThat(service.listAll()).containsExactly(t1, t2);
    }

    @Test
    @DisplayName("listByStatus: фильтрует по статусу")
    void listByStatusFilters() {
        Task t1 = new Task(1L, "a", "", Priority.LOW);
        Task t2 = new Task(2L, "b", "", Priority.LOW);
        Task t3 = new Task(3L, "c", "", Priority.LOW);
        t2.setStatus(TaskStatus.DONE);
        t3.setStatus(TaskStatus.DONE);
        when(repository.findAll()).thenReturn(Arrays.asList(t1, t2, t3));

        List<Task> done = service.listByStatus(TaskStatus.DONE);
        assertThat(done).containsExactlyInAnyOrder(t2, t3);
    }

    @Test
    @DisplayName("listByPriority: фильтрует по приоритету")
    void listByPriorityFilters() {
        Task t1 = new Task(1L, "a", "", Priority.LOW);
        Task t2 = new Task(2L, "b", "", Priority.HIGH);
        Task t3 = new Task(3L, "c", "", Priority.HIGH);
        when(repository.findAll()).thenReturn(Arrays.asList(t1, t2, t3));

        List<Task> high = service.listByPriority(Priority.HIGH);
        assertThat(high).containsExactlyInAnyOrder(t2, t3);
    }

    @Test
    @DisplayName("completionRatio: 0.0 для пустого списка")
    void completionRatioEmptyZero() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        assertThat(service.completionRatio()).isZero();
    }

    @Test
    @DisplayName("completionRatio: рассчитывает долю выполненных")
    void completionRatioComputesRatio() {
        Task t1 = new Task(1L, "a", "", Priority.LOW);
        Task t2 = new Task(2L, "b", "", Priority.LOW);
        Task t3 = new Task(3L, "c", "", Priority.LOW);
        Task t4 = new Task(4L, "d", "", Priority.LOW);
        t1.setStatus(TaskStatus.DONE);
        t2.setStatus(TaskStatus.DONE);
        when(repository.findAll()).thenReturn(Arrays.asList(t1, t2, t3, t4));

        assertThat(service.completionRatio()).isEqualTo(0.5);
    }

    @Test
    @DisplayName("конструктор: null репозиторий → NullPointerException")
    void constructorRejectsNull() {
        assertThatThrownBy(() -> new TaskService(null))
                .isInstanceOf(NullPointerException.class);
    }
}
