package ru.iteco.project.model;

import java.util.UUID;

/**
 * Модель данных пердставляющая роли пользователей
 */
public class UserRole implements Identified<UUID> {

    /*** Уникальный id роли пользователя */
    private UUID id;

    /*** Наименование роли пользователя */
    private String value;

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


    /**
     * Перечисление основных ролей пользователей
     */
    public enum UserRoleEnum {
        ADMIN,
        CUSTOMER,
        EXECUTOR;



        /**
         * Метод проверяет является ли входная строка текстовым представлением одного из элементов перечисления
         *
         * @param inputUserRole - текстовое представление роли пользователя
         * @return true - в перечислении присутствует аргумент с данным именем,
         * false - в перечислении отсутствует аргумент с данным именем
         */
        public static boolean isCorrectValue(String inputUserRole) {
            for (UserRoleEnum userRole : values()) {
                if (userRole.name().equals(inputUserRole)) {
                    return true;
                }
            }
            return false;
        }


        /**
         * Метод проверяет эквивалентна ли роль пользователя переданному значению роли пользователя
         *
         * @param userRoleEnum - Элемент перечисления доступных ролей пользователя
         * @param user     - сущность пользователя
         * @return true - роль пользоваателя эквивалентен переданному значению,
         * false - роль пользователя не эквивалентен переданному значению
         */
        public static boolean isEqualsUserRole(UserRoleEnum userRoleEnum, User user) {
            UserRole role = user.getRole();
            return (role != null) && isEqualsUserRole(userRoleEnum, role.getValue());
        }


        /**
         * Метод проверяет эквивалентна ли роль пользователя переданному значению роли пользователя
         *
         * @param userRoleEnum - Элемент перечисления доступных ролей пользователя
         * @param userRole - строковое представление роли пользователя
         * @return true - роль пользоваателя эквивалентен переданному значению,
         * false - роль пользователя не эквивалентен переданному значению
         */
        public static boolean isEqualsUserRole(UserRoleEnum userRoleEnum, String userRole) {
            if ((userRoleEnum != null) && isCorrectValue(userRole)) {
                return userRoleEnum == UserRoleEnum.valueOf(userRole);
            }
            return false;
        }
    }

}
