package ru.iteco.project.model;

/**
 * Перечисление возможных статусов задания
 */
public enum TaskStatus {

    TASK_REGISTERED("Задание зарегистрировано"),
    TASK_IN_PROGRESS("Задание на выполнении"),
    TASK_ON_CHECK("Задание на проверке"),
    TASK_ON_FIX("Задание на исправлении"),
    TASK_DONE("Задание выполнено"),
    TASK_CANCELED("Задание отменено");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    /**
     * Метод проверяет является ли входная строка текстовым представлением одного из элементов перечисления
     *
     * @param inputTaskStatus - текстовое представление роли
     * @return true - в перечислении присутствует аргумент с данным именем,
     * false - в перечислении отсутствует аргумент с данным именем
     */
    public static boolean isCorrectValue(String inputTaskStatus) {
        for (TaskStatus status : values()) {
            if (status.name().equals(inputTaskStatus)) {
                return true;
            }
        }
        return false;
    }
}
