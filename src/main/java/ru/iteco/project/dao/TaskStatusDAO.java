package ru.iteco.project.dao;

import ru.iteco.project.model.TaskStatus;
import ru.iteco.project.model.UserStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал DAO слоя для сущности TaskStatus
 */
public interface TaskStatusDAO extends GenericDAO<TaskStatus, UUID> {

    /**
     * Метод осуществляет поиск статуса задания по его текстовому представлению
     *
     * @param statusText - текстовое значение статуса задания
     * @return - объект статуса задания, соответствующий данному значению,
     * или null, если статуса с данным значением не существует
     */
    Optional<TaskStatus> findTaskStatusByValue(String statusText);


    /**
     * Метод получения статуса задания по id
     *
     * @param uuid - уникальный идентификатор статуса задания
     * @return Optional с объектом TaskStatus или с null, если такого статуса не существует
     */
    Optional<TaskStatus> findTaskStatusById(UUID uuid);


    /**
     * Метод проверяет существует ли статус задания с переданным текстовым предсталением
     *
     * @param statusText - текстовое значение статуса задания
     * @return - true - статус задания с данным значением существует,
     * false - статус с данным значением не существует
     */
    boolean taskStatusExist(String statusText);


    /**
     * Метод проверяет существование статуса задания с заданным id в системе
     *
     * @param uuid - уникальный идентификатор статуса задания
     * @return - true - статус с заданным id существует,
     * false - статус задания с заданным id не существует
     */
    boolean taskStatusWithIdIsExist(UUID uuid);

}
