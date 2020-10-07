package dao;

import model.Contract;
import model.Task;
import model.User;

import java.util.List;
import java.util.UUID;

public interface ContractDAO extends GenericDAO<Contract, UUID> {

    boolean contractIsExist(Contract contract);

    Contract findContractById(UUID contractId);

    Contract findContractByTask(Task task);

    List<Contract> findAllContractsByExecutor(User executor);


}
