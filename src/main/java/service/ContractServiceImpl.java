package service;

import dao.ContractDAO;
import model.Contract;
import model.ContractStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ContractServiceImpl implements ContractService {


    private static final Logger log = LogManager.getLogger(ContractServiceImpl.class.getName());

    private ContractDAO contractDAO;


    public void setContractDAO(ContractDAO contractDAO) {
        this.contractDAO = contractDAO;
    }

    @Override
    public void createContract(Contract contract) {
        contractDAO.save(contract);
        log.info("now: " + LocalDateTime.now() + " createContract: " + contract);
    }

    @Override
    public Contract deleteContract(Contract contract) {
        Contract deletedContract = contractDAO.delete(contract);
        log.info("now: " + LocalDateTime.now() + " deletedContract: " + deletedContract);
        return deletedContract;
    }

    @Override
    public void changeContractStatusTo(Contract contract, ContractStatus contractStatus) {
        contract.setContractStatus(contractStatus);
        contractDAO.update(contract);
        log.info("now: " + LocalDateTime.now() + " changeContractStatusTo: " + contract + "StatusTo: " + contractStatus);
    }

    @Override
    public ArrayList<Contract> getAllContracts() {
        return new ArrayList<>(contractDAO.getAll());
    }

    public ContractDAO getContractDAO() {
        return contractDAO;
    }
}
