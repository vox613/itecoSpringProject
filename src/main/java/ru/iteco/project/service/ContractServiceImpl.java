package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.controller.dto.ContractDtoRequest;
import ru.iteco.project.controller.dto.ContractDtoResponse;
import ru.iteco.project.dao.ContractDAO;
import ru.iteco.project.dao.TaskDAO;
import ru.iteco.project.dao.UserDAO;
import ru.iteco.project.model.*;
import ru.iteco.project.service.mappers.ContractDtoEntityMapper;
import ru.iteco.project.service.mappers.UserDtoEntityMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


import static ru.iteco.project.model.ContractStatus.ContractStatusEnum.*;
import static ru.iteco.project.model.TaskStatus.TaskStatusEnum.*;
import static ru.iteco.project.model.TaskStatus.TaskStatusEnum.DONE;
import static ru.iteco.project.model.TaskStatus.TaskStatusEnum.REGISTERED;
import static ru.iteco.project.model.UserRole.UserRoleEnum.EXECUTOR;
import static ru.iteco.project.model.UserRole.UserRoleEnum.isEqualsUserRole;
import static ru.iteco.project.model.UserStatus.UserStatusEnum.*;


/**
 * Класс реализует функционал сервисного слоя для работы с контрактами
 */
@Service
public class ContractServiceImpl implements ContractService {
    private static final Logger log = LogManager.getLogger(ContractServiceImpl.class.getName());

    private final ContractDAO contractDAO;
    private final UserDAO userDAO;
    private final TaskDAO taskDAO;
    private final ContractDtoEntityMapper contractMapper;
    private final UserDtoEntityMapper userDtoEntityMapper;
    private final TaskService taskService;


    public ContractServiceImpl(ContractDAO contractDAO, UserDAO userDAO, TaskDAO taskDAO, ContractDtoEntityMapper contractMapper,
                               UserDtoEntityMapper userDtoEntityMapper,  TaskService taskService) {
        this.contractDAO = contractDAO;
        this.userDAO = userDAO;
        this.taskDAO = taskDAO;
        this.contractMapper = contractMapper;
        this.userDtoEntityMapper = userDtoEntityMapper;
        this.taskService = taskService;
    }


    /**
     *  По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public List<ContractDtoResponse> getAllContracts() {
        ArrayList<ContractDtoResponse> contractDtoResponses = new ArrayList<>();
        for (Contract contract : contractDAO.getAll()) {
            contractDtoResponses.add(enrichContractInfo(contract));
        }
        return contractDtoResponses;
    }

    /**
     *  По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public ContractDtoResponse getContractById(UUID id) {
        ContractDtoResponse contractDtoResponse = null;
        Optional<Contract> optionalContract = contractDAO.findContractById(id);
        if (optionalContract.isPresent()) {
            Contract contract = optionalContract.get();
            contractDtoResponse = enrichContractInfo(contract);
        }
        return contractDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ContractDtoResponse createContract(ContractDtoRequest contractDtoRequest) {
        ContractDtoResponse contractDtoResponse = null;
        Optional<Task> taskById = taskDAO.findTaskById(contractDtoRequest.getTaskId());
        Optional<User> executorById = userDAO.findUserById(contractDtoRequest.getExecutorId());
        if (taskById.isPresent() && executorById.isPresent()) {
            Task task = taskById.get();
            User customer = task.getCustomer();
            User executor = executorById.get();

            if (isEqualsTaskStatus(REGISTERED, task)
                    && usersNotBlocked(customer, executor)
                    && isEqualsUserRole(EXECUTOR, executor)
                    && isCorrectConfirmCodes(contractDtoRequest.getConfirmationCode(), contractDtoRequest.getRepeatConfirmationCode())
                    && customerHaveEnoughMoney(task)) {

                customer.setWallet(customer.getWallet().subtract(task.getPrice()));
                userDAO.update(customer);
                userDAO.updateUserStatus(executor, ACTIVE);

                task.setLastTaskUpdateDate(LocalDateTime.now());
                task.setExecutor(executor);
                taskDAO.updateTaskStatus(task, IN_PROGRESS);

                Contract contract = contractMapper.requestDtoToEntity(contractDtoRequest);
                contract.setExecutor(executor);
                contract.setTask(task);
                contract.setCustomer(task.getCustomer());
                contractDAO.save(contract);

                contractDtoResponse = enrichContractInfo(contract);
            }
        }
        return contractDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ContractDtoResponse updateContract(UUID id, ContractDtoRequest contractDtoRequest) {
        ContractDtoResponse contractDtoResponse = null;
        if (Objects.equals(id, contractDtoRequest.getId())
                && contractDtoRequest.getUserId() != null
                && contractDAO.contractWithIdIsExist(id)) {

            Optional<User> userOptional = userDAO.findUserById(contractDtoRequest.getUserId());
            Optional<Contract> contractById = contractDAO.findContractById(id);
            if (userOptional.isPresent() && contractById.isPresent()) {
                User user = userOptional.get();
                Contract contract = contractById.get();

                if (allowToUpdate(user, contract)) {
                    contractMapper.requestDtoToEntity(contractDtoRequest, contract, user.getRole().getValue());
                    transferFunds(contract);
                    userDAO.update(contract.getCustomer());
                    userDAO.update(contract.getExecutor());
                    contractDAO.update(contract);
                    contractDtoResponse = enrichContractInfo(contract);
                }
            }
        }
        return contractDtoResponse;
    }


    /**
     *  SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     *  REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     *  должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteContract(UUID id) {
        return contractDAO.deleteByPK(id) != null;
    }


    @Override
    public ContractDtoResponse enrichContractInfo(Contract contract) {
        ContractDtoResponse contractDtoResponse = contractMapper.entityToResponseDto(contract);
        contractDtoResponse.setCustomer(userDtoEntityMapper.entityToResponseDto(contract.getCustomer()));
        contractDtoResponse.setExecutor(userDtoEntityMapper.entityToResponseDto(contract.getExecutor()));
        contractDtoResponse.setTask(taskService.enrichByUsersInfo(contract.getTask()));
        return contractDtoResponse;
    }


    public ContractDAO getContractDAO() {
        return contractDAO;
    }

    /**
     * Метод осуществляет операция перечисления денежных средств на счет заказчика или исполнителя
     * в зависимости от статуса договора в который он переводится
     *
     * @param contract - объект договора
     */
    private void transferFunds(Contract contract) {
        if (isEqualsContractStatus(ContractStatus.ContractStatusEnum.DONE, contract)) {
            User executor = contract.getExecutor();
            executor.setWallet(executor.getWallet().add(contract.getTask().getPrice()));
        } else if (isEqualsContractStatus(TERMINATED, contract)) {
            User customer = contract.getCustomer();
            customer.setWallet(customer.getWallet().add(contract.getTask().getPrice()));
        }
    }

    /**
     * Метод проверяет достаточно ли у заказчика денег для формирования договора оказания услуги
     *
     * @param task - задание
     * @return - true - достаточно средств, false - недостаточно средств
     */
    private boolean customerHaveEnoughMoney(Task task) {
        BigDecimal customerWallet = task.getCustomer().getWallet();
        BigDecimal taskPrice = task.getPrice();
        return customerWallet.compareTo(taskPrice) >= 0;
    }

    /**
     * Метод проверяет статус пользователей
     *
     * @param customer- заказчик
     * @param executor  - исполниель
     * @return - true - пользователи не заблокированы, false - пользователи заблокированы
     */
    private boolean usersNotBlocked(User customer, User executor) {
        return !(isEqualsUserStatus(BLOCKED, customer) || isEqualsUserStatus(BLOCKED, executor));
    }


    /**
     * Метод проверяет правильность введенных кодов подтверждения
     *
     * @param code       - основной код
     * @param repeatCode - код подтверждения
     * @return - true - коды совпадают, false - коды не совпадают
     */
    private boolean isCorrectConfirmCodes(String code, String repeatCode) {
        return (code != null) && code.equals(repeatCode);
    }

    /**
     * Метод проверяет возможность обновления контракта
     *
     * @param user     - пользователь инициировавший процесс
     * @param contract - контракт
     * @return - true - пользователь не заблокирован, пользователь - заказчик, задание находится в финальном статусе,
     * контракт оплачен, false - в любом ином случае
     */
    private boolean allowToUpdate(User user, Contract contract) {
        boolean userNotBlocked = !isEqualsUserStatus(BLOCKED, user);
        boolean userIsCustomer = user.getId().equals(contract.getCustomer().getId());
        boolean contractIsPaid = isEqualsContractStatus(PAID, contract);
        boolean taskInTerminatedStatus = isEqualsTaskStatus(DONE, contract.getTask())
                || isEqualsTaskStatus(CANCELED, contract.getTask()) ;

        return userNotBlocked && userIsCustomer && contractIsPaid && taskInTerminatedStatus;
    }
}
