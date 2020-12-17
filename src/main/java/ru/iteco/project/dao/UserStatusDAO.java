package ru.iteco.project.dao;

import ru.iteco.project.model.UserStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал DAO слоя для сущности UserStatus
 */
public interface UserStatusDAO extends GenericDAO<UserStatus, UUID> {

    /**
     * Метод осуществляет поиск статуса пользователя по его текстовому представлению
     *
     * @param statusText - текстовое значение статуса пользователя
     * @return - объект статуса пользователя, соответствующий данному значению,
     * или null, если статуса с данным значением не существует
     */
    Optional<UserStatus> findUserStatusByValue(String statusText);


    /**
     * Метод получения статуса пользователя по id
     *
     * @param uuid - уникальный идентификатор статуса пользователя
     * @return Optional с объектом UserStatus или с null, если такого статуса не существует
     */
    Optional<UserStatus> findUserStatusById(UUID uuid);


    /**
     * Метод проверяет существует ли статус пользователя с переданным текстовым предсталением
     *
     * @param statusText - текстовое значение статуса пользователя
     * @return - true - статус пользователя с данным значением существует,
     * false - статус с данным значением не существует
     */
    boolean userStatusExist(String statusText);


    /**
     * Метод проверяет существование статуса пользователя с заданным id в системе
     *
     * @param uuid - уникальный идентификатор статуса пользователя
     * @return - true - статус с заданным id существует,
     * false - статус пользователя с заданным id не существует
     */
    boolean userStatusWithIdIsExist(UUID uuid);

}
