package service;

import model.Contract;
import model.ContractStatus;

import java.util.ArrayList;

public interface ContractService {

    void createContract(Contract contract);

    Contract deleteContract(Contract contract);

    void changeContractStatusTo(Contract contract, ContractStatus contractStatus);

    ArrayList<Contract> getAllContracts();

}
