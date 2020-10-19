package ru.iteco.project.model;

/**
 * Перечисление возможных статусов в которых может находитья пользователь
 */
public enum UserStatus {

    STATUS_CREATED("Создан"),
    STATUS_LOCKED("Заблокирован"),
    STATUS_ACTIVE("Активен");

    private String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
