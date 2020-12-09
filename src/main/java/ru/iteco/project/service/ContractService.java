package ru.iteco.project.service;

import ru.iteco.project.controller.dto.ContractDtoRequest;
import ru.iteco.project.controller.dto.ContractDtoResponse;
import ru.iteco.project.domain.Contract;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности Contract
 */
public interface ContractService {

    /**
     * Метод получает вссе договоры из коллекции
     *
     * @return - список всех договоров из коллекции
     */
    List<ContractDtoResponse> getAllContracts();

    /**
     * Метод получения Контракта по его id
     *
     * @param id - уникальный идентификатор Контракта
     * @return ContractDtoResponse - dto объект с данными о контракте
     */
    ContractDtoResponse getContractById(UUID id);

    /**
     * Метод создания контракта
     *
     * @param contractDtoRequest - тело запроса с данными для создания контракта
     * @return ContractDtoResponse - dto объект с данными о контракте
     */
    ContractDtoResponse createContract(ContractDtoRequest contractDtoRequest);

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

    /**
     * Метод обогащает ContractDtoResponse данными о заказчике, исполнителе и задании
     *
     * @param contract - объект задания
     */
    ContractDtoResponse enrichContractInfo(Contract contract);


}
