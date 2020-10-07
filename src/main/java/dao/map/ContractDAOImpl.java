package dao.map;

import dao.ContractDAO;
import model.Contract;
import model.Task;
import model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ContractDAOImpl extends AbstractDao<Contract, UUID> implements ContractDAO {

    public ContractDAOImpl() {
        super(Contract.class, new HashMap<>());
    }


    @Override
    public boolean contractIsExist(Contract enterContract) {
        if (enterContract != null) {
            UUID id = enterContract.getId();
            return elements.containsKey(id) && enterContract.equals(elements.get(id));
        }
        return false;
    }

    @Override
    public Contract findContractById(UUID contractId) {
        return getByPK(contractId);
    }

    @Override
    public Contract findContractByTask(Task task) {
        for (Contract contract : elements.values()) {
            if ((contract != null) && contract.getTask().equals(task)) {
                return contract;
            }
        }
        return null;
    }

    @Override
    public List<Contract> findAllContractsByExecutor(User executor) {
        ArrayList<Contract> contracts = new ArrayList<>();
        for (Contract contract : elements.values()) {
            if ((contract != null) && contract.getExecutor().equals(executor)) {
                contracts.add(contract);
            }
        }
        return contracts;
    }
}
