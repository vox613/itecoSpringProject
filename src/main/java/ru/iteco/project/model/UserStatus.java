package ru.iteco.project.model;

/**
 * Перечисление возможных статусов в которых может находитья пользователь
 */
public enum UserStatus {

    NOT_EXIST("Пользователя не существует"),
    CREATED("Создан"),
    BLOCKED("Заблокирован"),
    ACTIVE("Активен");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Метод проверяет является ли входная строка текстовым представлением одного из элементов перечисления
     *
     * @param inputUserStatus - текстовое представление роли
     * @return true - в перечислении присутствует аргумент с данным именем,
     * false - в перечислении отсутствует аргумент с данным именем
     */
    public static boolean isCorrectValue(String inputUserStatus) {
        for (UserStatus userStatus : values()) {
            if (userStatus.name().equals(inputUserStatus)) {
                return true;
            }
        }
        return false;
    }
}
