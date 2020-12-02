package ru.iteco.project.service;

import ru.iteco.project.controller.dto.ContractDtoRequest;
import ru.iteco.project.controller.dto.ContractDtoResponse;
import ru.iteco.project.model.Contract;
import ru.iteco.project.model.ContractStatus;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности Contract
 */
public interface ContractService {

    /**
     * Метод сохранения договора в коллекцию
     *
     * @param contract - договор для сохраннения
     */
    void createContract(Contract contract);

    /**
     * Метод удаления из коллекции переданного договора
     *
     * @param contract - договор для удаления
     * @return - удаленный договор
     */
    Contract deleteContract(Contract contract);

    /**
     * Метод изменения статуса договора на переданный в агументах
     *
     * @param contract       - договор статус которого необходимо изменить
     * @param contractStatus - статус на которой меняется состояние договора
     */
    void changeContractStatusTo(Contract contract, ContractStatus contractStatus);

    /**
     * Метод получает вссе договоры из коллекции
     *
     * @return - список всех договоров из коллекции
     */
    List<ContractDtoResponse> getAllContracts();

    /**
     * Метод получения Контракта по его id
     * @param id - уникальный идентификатор Контракта
     * @return ContractDtoResponse - dto объект с данными о контракте
     */
    ContractDtoResponse getContractById(UUID id);

    /**
     * Метод создания контракта
     * @param contractDtoRequest - тело запроса с данными для создания контракта
     * @return ContractDtoResponse - dto объект с данными о контракте
     */
    ContractDtoRequest createContract(ContractDtoRequest contractDtoRequest);

    /**
     * Метод обновления существующего контракта
     *
     * @param id                 - уникальный идентификатор Контракта
     * @param contractDtoRequest -  тело запроса для обновления
     * @return ContractDtoResponse - dto объект с данными о контракте
     */
    ContractDtoResponse updateContract(UUID id, ContractDtoRequest contractDtoRequest);

    /**
     * Метод удаляет договор из коллекции
     *
     * @param id - id договора для удаления
     * @return - true - контракт успешно удален,
     * false - произошла ошибка при удалении контракта/контракта не существует
     */
    Boolean deleteContract(UUID id);

}
