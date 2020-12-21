package ru.iteco.project.model;

import java.util.UUID;

/**
 * Модель данных представляющая статусы пользователей
 */
public class UserStatus implements Identified<UUID> {

    /*** Уникальный id роли пользователя */
    private UUID id;

    /*** Наименование роли пользователя */
    private String value;

    /*** Описание роли пользователя */
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
     * Перечисление возможных статусов в которых может находитья пользователь
     */
    public enum UserStatusEnum {
        NOT_EXIST("Пользователя не существует"),
        CREATED("Создан"),
        BLOCKED("Заблокирован"),
        ACTIVE("Активен");

        private final String description;

        UserStatusEnum(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        /**
         * Метод проверяет является ли входная строка текстовым представлением одного из элементов перечисления
         *
         * @param inputUserStatus - текстовое представление статуса пользователя
         * @return true - в перечислении присутствует аргумент с данным именем,
         * false - в перечислении отсутствует аргумент с данным именем
         */
        public static boolean isCorrectValue(String inputUserStatus) {
            for (UserStatusEnum userStatus : values()) {
                if (userStatus.name().equals(inputUserStatus)) {
                    return true;
                }
            }
            return false;
        }


        /**
         * Метод проверяет эквивалентен ли статус пользователя переданному значению статуса пользователя
         *
         * @param userStatusEnum - Элемент перечисления доступных статусов пользователя
         * @param user           - сущность пользователя
         * @return true - статус пользоваателя эквивалентен переданному хначению,
         * false - статус пользователя не эквивалентен переданному значению
         */
        public static boolean isEqualsUserStatus(UserStatusEnum userStatusEnum, User user) {
            UserStatus userStatus = user.getUserStatus();
            return (userStatus != null) && isEqualsUserStatus(userStatusEnum, userStatus.getValue());
        }


        /**
         * Метод проверяет эквивалентен ли статус пользователя переданному значению статуса пользователя
         *
         * @param userStatusEnum - Элемент перечисления доступных статусов пользователя
         * @param userStatus     - строковое представление статуса пользователя
         * @return true - статус пользоваателя эквивалентен переданному хначению,
         * false - статус пользователя не эквивалентен переданному значению
         */
        public static boolean isEqualsUserStatus(UserStatusEnum userStatusEnum, String userStatus) {
            if ((userStatusEnum != null) && isCorrectValue(userStatus)) {
                return userStatusEnum == UserStatusEnum.valueOf(userStatus);
            }
            return false;
        }
    }

}
