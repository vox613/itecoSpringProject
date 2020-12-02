package ru.iteco.project.dao;

import ru.iteco.project.model.UserRole;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал DAO слоя для сущности UserRole
 */
public interface UserRoleDAO extends GenericDAO<UserRole, UUID> {

    /**
     * Метод осуществляет поиск роли пользователя по ее текстовому представлению
     *
     * @param roleText - текстовое значение роли пользователя
     * @return - объект роли пользователя, соответствующий данному значению,
     * или null, если роли с данным значением не существует
     */
    Optional<UserRole> findUserRoleByValue(String roleText);


    /**
     * Метод получения роли пользователя по id
     *
     * @param uuid - уникальный идентификатор роли пользователя
     * @return Optional с объектом UserRole или с null, если такой роли не существует
     */
    Optional<UserRole> findUserRoleById(UUID uuid);


    /**
     * Метод проверяет существует ли роль пользователя с переданным текстовым предсталением
     *
     * @param roleText - текстовое значение роли пользователя
     * @return - true - роль пользователь с данным значением существует,
     * false - роль с данным значением не существует
     */
    boolean userRoleExist(String roleText);


    /**
     * Метод проверяет существование роли пользователя с заданным id в системе
     *
     * @param uuid - уникальный идентификатор роли пользователя
     * @return - true - роли с заданным id существует,
     * false - роли пользователя с заданным id не существует
     */
    boolean userRoleWithIdIsExist(UUID uuid);

}
