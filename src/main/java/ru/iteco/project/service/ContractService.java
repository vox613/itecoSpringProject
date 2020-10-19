package ru.iteco.project.service;

import ru.iteco.project.model.Contract;
import ru.iteco.project.model.ContractStatus;

import java.util.ArrayList;

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
    ArrayList<Contract> getAllContracts();

}
