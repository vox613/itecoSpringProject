package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.ContractDtoRequest;
import ru.iteco.project.controller.dto.ContractDtoResponse;
import ru.iteco.project.dao.ContractDAO;
import ru.iteco.project.dao.TaskDAO;
import ru.iteco.project.dao.UserDAO;
import ru.iteco.project.model.*;
import ru.iteco.project.service.mappers.ContractDtoEntityMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс реализует функционал сервисного слоя для работы с контрактами
 */
@Service
public class ContractServiceImpl implements ContractService {


    private static final Logger log = LogManager.getLogger(ContractServiceImpl.class.getName());

    private final UserDAO userDAO;
    private final ContractDAO contractDAO;
    private final TaskDAO taskDAO;
    private final ContractDtoEntityMapper contractMapper;
    private final TaskService taskService;
    private final UserService userService;


    @Autowired
    public ContractServiceImpl(UserDAO userDAO, ContractDAO contractDAO, TaskDAO taskDAO,
                               ContractDtoEntityMapper contractMapper, TaskService taskService,
                               UserService userService) {
        this.userDAO = userDAO;
        this.contractDAO = contractDAO;
        this.taskDAO = taskDAO;
        this.contractMapper = contractMapper;
        this.taskService = taskService;
        this.userService = userService;
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
    public List<ContractDtoResponse> getAllContracts() {
        ArrayList<ContractDtoResponse> contractDtoResponses = new ArrayList<>();
        for (Contract contract : contractDAO.getAll()) {
            contractDtoResponses.add(getContractById(contract.getId()));
        }
        return contractDtoResponses;
    }

    @Override
    public ContractDtoResponse getContractById(UUID id) {
        ContractDtoResponse contractDtoResponse = new ContractDtoResponse();
        Optional<Contract> optionalContract = contractDAO.findContractById(id);
        if (optionalContract.isPresent()) {
            Contract contract = optionalContract.get();
            contractDtoResponse = contractMapper.entityToResponseDto(contract);
            contractDtoResponse.setCustomer(userService.getUserById(contract.getCustomer().getId()));
            contractDtoResponse.setExecutor(userService.getUserById(contract.getExecutor().getId()));
            contractDtoResponse.setTask(taskService.getTaskById(contract.getTask().getId()));
        }
        return contractDtoResponse;
    }

    @Override
    public ContractDtoRequest createContract(ContractDtoRequest contractDtoRequest) {
        Optional<Task> taskById = taskDAO.findTaskById(contractDtoRequest.getTaskId());
        Optional<User> executorById = userDAO.findUserById(contractDtoRequest.getExecutorId());
        if (taskById.isPresent() && executorById.isPresent()) {
            Task task = taskById.get();
            User customer = task.getCustomer();
            User executor = executorById.get();

            if (TaskStatus.TASK_REGISTERED.equals(task.getTaskStatus())
                    && usersNotBlocked(customer, executor)
                    && respectiveUserRole(executor)
                    && isCorrectConfirmCodes(contractDtoRequest.getConfirmationCode(), contractDtoRequest.getRepeatConfirmationCode())
                    && customerHaveEnoughMoney(task)) {

                customer.setWallet(customer.getWallet().subtract(task.getPrice()));
                userDAO.save(customer);

                executor.setUserStatus(UserStatus.STATUS_ACTIVE);
                userDAO.save(executor);

                Contract contract = contractMapper.requestDtoToEntity(contractDtoRequest);
                contract.setContractStatus(ContractStatus.CONTRACT_PAID);
                contractDAO.save(contract);

                task.setTaskStatus(TaskStatus.TASK_IN_PROGRESS);
                task.setLastTaskUpdateDate(LocalDateTime.now());
                task.setExecutor(executor);
                taskDAO.save(task);

                contractDtoRequest.setId(contract.getId());
                contractDtoRequest.setContractStatus(contract.getContractStatus().name());
            }
        }
        return contractDtoRequest;
    }

    @Override
    public void updateContract(UUID id, UUID userId, ContractDtoRequest contractDtoRequest) {
        if (contractDAO.contractWithIdIsExist(id) && Objects.equals(id, contractDtoRequest.getId())) {
            Optional<User> userOptional = userDAO.findUserById(userId);
            Optional<Contract> contractById = contractDAO.findContractById(id);
            if (userOptional.isPresent() && contractById.isPresent()) {
                User user = userOptional.get();
                Contract contract = contractById.get();
                if (allowToUpdate(user, contract)) {
                    contractMapper.requestDtoToEntity(contractDtoRequest, contract, user.getRole());
                    contractDAO.update(contract);
                }
            }
        }
    }

    @Override
    public ContractDtoResponse deleteContract(UUID id) {
        ContractDtoResponse contractDtoResponse = getContractById(id);
        if (contractDtoResponse.getId() != null) {
            contractDAO.deleteByPK(id);
        }
        return contractDtoResponse;
    }


    public ContractDAO getContractDAO() {
        return contractDAO;
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
        return !(UserStatus.STATUS_BLOCKED.equals(customer.getUserStatus()) ||
                UserStatus.STATUS_BLOCKED.equals(executor.getUserStatus()));
    }

    /**
     * Метод проверяет соответствие роли пользователя необходимой
     *
     * @param user - пользователь
     * @return - true - пользователь имеет роль исполнителя, false -пользователь не имеет роль исполнителя
     */
    private boolean respectiveUserRole(User user) {
        return Role.ROLE_EXECUTOR.equals(user.getRole());
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
        boolean userNotBlocked = !UserStatus.STATUS_BLOCKED.equals(user.getUserStatus());
        boolean userIsCustomer = user.getId().equals(contract.getCustomer().getId());
        boolean contractIsPaid = ContractStatus.CONTRACT_PAID.equals(contract.getContractStatus());
        boolean taskInTerminatedStatus = TaskStatus.TASK_DONE.equals(contract.getTask().getTaskStatus())
                || TaskStatus.TASK_CANCELED.equals(contract.getTask().getTaskStatus());

        return userNotBlocked && userIsCustomer && taskInTerminatedStatus && contractIsPaid;
    }
}
