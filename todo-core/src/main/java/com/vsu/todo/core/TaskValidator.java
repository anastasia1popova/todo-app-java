package com.vsu.todo.core;

/**
 * Вспомогательный класс для валидации входных данных задачи.
 */
public final class TaskValidator {

    private static final int MAX_TITLE_LENGTH = 200;
    private static final int MAX_DESCRIPTION_LENGTH = 2000;

    private TaskValidator() {
        // utility class
    }

    public static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must not be blank");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException(
                    "Title length must not exceed " + MAX_TITLE_LENGTH);
        }
    }

    public static void validateDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "Description length must not exceed " + MAX_DESCRIPTION_LENGTH);
        }
    }

    public static boolean isValidId(long id) {
        return id > 0;
    }
}
