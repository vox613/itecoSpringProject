package ru.iteco.project.model;

/**
 * Перечисление возможных статусов задания
 */
public enum TaskStatus {

    TASK_REGISTERED("Задание зарегистрировано"),
    TASK_IN_PROGRESS("Задание на выполнении"),
    TASK_CANCELED("Задание отменено"),
    TASK_DONE("Задание выполнено");

    private String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}
