package ru.iteco.project.dao;

import ru.iteco.project.model.ContractStatus;
import ru.iteco.project.model.TaskStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал DAO слоя для сущности ContractStatus
 */
public interface ContractStatusDAO extends GenericDAO<ContractStatus, UUID> {

    /**
     * Метод осуществляет поиск статуса контракта по его текстовому представлению
     *
     * @param statusText - текстовое значение статуса контракта
     * @return - объект статуса контракта, соответствующий данному значению,
     * или null, если статуса с данным значением не существует
     */
    Optional<ContractStatus> findContractStatusByValue(String statusText);


    /**
     * Метод получения статуса контракта по id
     *
     * @param uuid - уникальный идентификатор статуса контракта
     * @return Optional с объектом ContractStatus или с null, если такого статуса не существует
     */
    Optional<ContractStatus> findContractStatusById(UUID uuid);


    /**
     * Метод проверяет существует ли статус контракта с переданным текстовым предсталением
     *
     * @param statusText - текстовое значение статуса контракта
     * @return - true - статус контракта с данным значением существует,
     * false - статус с данным значением не существует
     */
    boolean contractStatusExist(String statusText);


    /**
     * Метод проверяет существование статуса контракта с заданным id в системе
     *
     * @param uuid - уникальный идентификатор статуса контракта
     * @return - true - статус с заданным id существует,
     * false - статус контракта с заданным id не существует
     */
    boolean contractStatusWithIdIsExist(UUID uuid);

}
