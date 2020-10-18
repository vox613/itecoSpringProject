package ru.iteco.project.service;

import ru.iteco.project.model.Contract;
import ru.iteco.project.model.ContractStatus;

import java.util.ArrayList;

public interface ContractService {

    void createContract(Contract contract);

    Contract deleteContract(Contract contract);

    void changeContractStatusTo(Contract contract, ContractStatus contractStatus);

    ArrayList<Contract> getAllContracts();

}
