package ru.iteco.project.dao;

import ru.iteco.project.model.Contract;
import ru.iteco.project.model.Task;
import ru.iteco.project.model.User;

import java.util.List;
import java.util.UUID;

public interface ContractDAO extends GenericDAO<Contract, UUID> {

    boolean contractIsExist(Contract contract);

    Contract findContractById(UUID contractId);

    Contract findContractByTask(Task task);

    List<Contract> findAllContractsByExecutor(User executor);


}
