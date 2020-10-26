package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iteco.project.dao.ContractDAO;
import ru.iteco.project.model.Contract;
import ru.iteco.project.model.ContractStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Класс реализует функционал сервисного слоя для работы с контрактами
 */
@Service
public class ContractServiceImpl implements ContractService {


    private static final Logger log = LogManager.getLogger(ContractServiceImpl.class.getName());

    private ContractDAO contractDAO;


    @Autowired
    public ContractServiceImpl(ContractDAO contractDAO) {
        this.contractDAO = contractDAO;
    }

    /**
     * Метод сохранения договора в коллекцию
     *
     * @param contract - договор для сохраннения
     */
    @Override
    public void createContract(Contract contract) {
        contractDAO.save(contract);
        log.info("now: " + LocalDateTime.now() + " createContract: " + contract);
    }

    /**
     * Метод удаления из коллекции переданного договора
     *
     * @param contract - договор для удаления
     * @return - удаленный договор
     */
    @Override
    public Contract deleteContract(Contract contract) {
        Contract deletedContract = contractDAO.delete(contract);
        log.info("now: " + LocalDateTime.now() + " deletedContract: " + deletedContract);
        return deletedContract;
    }

    /**
     * Метод изменения статуса договора на переданный в агументах
     *
     * @param contract       - договор статус которого необходимо изменить
     * @param contractStatus - статус на которой меняется состояние договора
     */
    @Override
    public void changeContractStatusTo(Contract contract, ContractStatus contractStatus) {
        contract.setContractStatus(contractStatus);
        contractDAO.update(contract);
        log.info("now: " + LocalDateTime.now() + " changeContractStatusTo: " + contract + "StatusTo: " + contractStatus);
    }

    /**
     * Метод получает все договоры из коллекции
     *
     * @return - список всех договоров из коллекции
     */
    @Override
    public ArrayList<Contract> getAllContracts() {
        return new ArrayList<>(contractDAO.getAll());
    }

    public ContractDAO getContractDAO() {
        return contractDAO;
    }
}
