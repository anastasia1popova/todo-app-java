package com.vsu.todo.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TaskValidator")
class TaskValidatorTest {

    @Test
    @DisplayName("корректный title не вызывает исключений")
    void validTitlePasses() {
        assertThatCode(() -> TaskValidator.validateTitle("OK"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("пустой / null title отбрасываются")
    void blankTitleRejected() {
        assertThatThrownBy(() -> TaskValidator.validateTitle(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TaskValidator.validateTitle("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("слишком длинный title отбрасывается")
    void tooLongTitleRejected() {
        String longTitle = "x".repeat(201);
        assertThatThrownBy(() -> TaskValidator.validateTitle(longTitle))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("null description допустим, очень длинный — нет")
    void descriptionRules() {
        assertThatCode(() -> TaskValidator.validateDescription(null))
                .doesNotThrowAnyException();
        assertThatCode(() -> TaskValidator.validateDescription("normal text"))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> TaskValidator.validateDescription("x".repeat(2001)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("isValidId возвращает true только для положительных id")
    void idValidation() {
        assertThat(TaskValidator.isValidId(1)).isTrue();
        assertThat(TaskValidator.isValidId(0)).isFalse();
        assertThat(TaskValidator.isValidId(-5)).isFalse();
    }
}
