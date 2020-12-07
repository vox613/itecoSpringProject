package ru.iteco.project.model;

/**
 * Перечисление возможных ролей пользователей
 */
public enum Role {

    ROLE_ADMIN,
    ROLE_CUSTOMER,
    ROLE_EXECUTOR;

    /**
     * Метод проверяет является ли входная строка текстовым представлением одного из элементов перечисления
     *
     * @param inputRole - текстовое представление роли
     * @return true - в перечислении присутствует аргумент с данным именем,
     * false - в перечислении отсутствует аргумент с данным именем
     */
    public static boolean isCorrectValue(String inputRole) {
        for (Role role : values()) {
            if (role.name().equals(inputRole)) {
                return true;
            }
        }
        return false;
    }

}
