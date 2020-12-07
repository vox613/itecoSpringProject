package ru.iteco.project.dao.map;

import org.springframework.stereotype.Repository;
import ru.iteco.project.dao.ContractDAO;
import ru.iteco.project.model.Contract;
import ru.iteco.project.model.Task;
import ru.iteco.project.model.User;

import java.util.*;

/**
 * Класс реализующий функционал доступа к данным о договорах
 */
@Repository
public class ContractDAOImpl extends AbstractDao<Contract, UUID> implements ContractDAO {

    public ContractDAOImpl() {
        super(Contract.class, new HashMap<>());
    }

    /**
     * Метод проверяет существует ли в коллекции переданный в аргументах контракт
     *
     * @param enterContract - контракт, существование которого проверяется
     * @return - true - контракт присутстует в коллекции, false - контракт отсутствует в коллекции
     */
    @Override
    public boolean contractIsExist(Contract enterContract) {
        if (enterContract != null) {
            UUID id = enterContract.getId();
            return elements.containsKey(id) && enterContract.equals(elements.get(id));
        }
        return false;
    }

    /**
     * Метод осуществляет поиск контракта по уникальному id
     *
     * @param contractId - уникальный id контракта
     * @return - объект контракта, соответствующий данному id, или null, если контракта нет в коллекции
     */
    @Override
    public Optional<Contract> findContractById(UUID contractId) {
        return Optional.ofNullable(getByPK(contractId));
    }

    /**
     * Метод осуществляет поиск контракта по заданию
     *
     * @param task - задание, контракт для которого необходимо найти
     * @return - объект контракта, соответствующий данному заданию, или null, если контракта нет в коллекции
     */
    @Override
    public Contract findContractByTask(Task task) {
        for (Contract contract : elements.values()) {
            if ((contract != null) && contract.getTask().getId().equals(task.getId())) {
                return contract;
            }
        }
        return null;
    }

    /**
     * Метод осуществляет поиск всех договоров данного  исполнителя
     *
     * @param executor - исполнитель, контракты которого необходимо найти
     * @return - список всех договоров исполнителя
     */
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

    /**
     * Метод проверяет существование контракта с заданным id
     *
     * @param uuid - уникальный идентификатор контракта
     * @return true - искомый контракт существует, false - искомый договор не существует
     */
    @Override
    public boolean contractWithIdIsExist(UUID uuid) {
        return Optional.ofNullable(getByPK(uuid)).isPresent();
    }
}
