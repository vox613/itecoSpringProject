package ru.iteco.project.model;

import java.util.UUID;

/**
 * Модель данных пердставляющая статусы задания
 */
public class TaskStatus implements Identified<UUID> {

    /*** Уникальный id статуса задания */
    private UUID id;

    /*** Наименование статуса задания */
    private String value;

    /*** Описание статуса задания */
    private String description;

    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Перечисление возможных статусов задания
     */
    public enum TaskStatusEnum {
        REGISTERED("Задание зарегистрировано"),
        IN_PROGRESS("Задание на выполнении"),
        ON_CHECK("Задание на проверке"),
        ON_FIX("Задание на исправлении"),
        DONE("Задание выполнено"),
        CANCELED("Задание отменено");

        private final String description;

        TaskStatusEnum(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }


        /**
         * Метод проверяет является ли входная строка текстовым представлением одного из элементов перечисления
         *
         * @param inputTaskStatus - текстовое представление статуса заданя
         * @return true - в перечислении присутствует аргумент с данным именем,
         * false - в перечислении отсутствует аргумент с данным именем
         */
        public static boolean isCorrectValue(String inputTaskStatus) {
            for (TaskStatusEnum statusEnum : values()) {
                if (statusEnum.name().equals(inputTaskStatus)) {
                    return true;
                }
            }
            return false;
        }


        /**
         * Метод проверяет эквивалентен ли статус задания переданному значению статуса задания
         *
         * @param taskStatusEnum - Элемент перечисления доступных статусов задания
         * @param task           - сущность задания
         * @return true - статус задания эквивалентен переданному значению,
         * false - статус задания не эквивалентен переданному значению
         */
        public static boolean isEqualsTaskStatus(TaskStatusEnum taskStatusEnum, Task task) {
            TaskStatus taskStatus = task.getTaskStatus();
            return (taskStatus != null) && isEqualsTaskStatus(taskStatusEnum, taskStatus.getValue());
        }


        /**
         * Метод проверяет эквивалентен ли статус задания переданному значению статуса задания
         *
         * @param taskStatusEnum - Элемент перечисления доступных статусов задания
         * @param taskStatus     - строковое представление статуса задания
         * @return true - статус задания эквивалентен переданному значению,
         * false - статус задания не эквивалентен переданному значению
         */
        public static boolean isEqualsTaskStatus(TaskStatusEnum taskStatusEnum, String taskStatus) {
            if ((taskStatusEnum != null) && isCorrectValue(taskStatus)) {
                return taskStatusEnum == TaskStatusEnum.valueOf(taskStatus);
            }
            return false;
        }
    }

}
