package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.controller.dto.TaskDtoRequest;
import ru.iteco.project.controller.dto.TaskDtoResponse;
import ru.iteco.project.dao.ContractDAO;
import ru.iteco.project.dao.TaskDAO;
import ru.iteco.project.dao.UserDAO;
import ru.iteco.project.exception.UnavailableRoleOperationException;
import ru.iteco.project.model.Contract;
import ru.iteco.project.model.Task;
import ru.iteco.project.model.User;
import ru.iteco.project.service.mappers.TaskDtoEntityMapper;
import ru.iteco.project.service.mappers.UserDtoEntityMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.iteco.project.model.TaskStatus.TaskStatusEnum.*;
import static ru.iteco.project.model.UserRole.UserRoleEnum.EXECUTOR;
import static ru.iteco.project.model.UserRole.UserRoleEnum.isEqualsUserRole;
import static ru.iteco.project.model.UserStatus.UserStatusEnum.*;

/**
 * Класс реализует функционал сервисного слоя для работы с заданиями
 */
@Service
@PropertySource(value = {"classpath:errors.properties"})
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LogManager.getLogger(TaskServiceImpl.class.getName());

    @Value("${errors.user.role.operation.unavailable}")
    private String unavailableOperationMessage;

    private final TaskDAO taskDAO;
    private final UserDAO userDAO;
    private final ContractDAO contractDAO;
    private final TaskDtoEntityMapper taskMapper;
    private final UserDtoEntityMapper userMapper;


    public TaskServiceImpl(TaskDAO taskDAO, UserDAO userDAO, ContractDAO contractDAO,
                           TaskDtoEntityMapper taskMapper, UserDtoEntityMapper userMapper) {
        this.taskDAO = taskDAO;
        this.userDAO = userDAO;
        this.contractDAO = contractDAO;
        this.taskMapper = taskMapper;
        this.userMapper = userMapper;
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskDtoResponse> getAllTasks() {
        ArrayList<TaskDtoResponse> taskDtoResponses = new ArrayList<>();
        for (Task task : taskDAO.getAll()) {
            taskDtoResponses.add(enrichByUsersInfo(task));
        }
        return taskDtoResponses;
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskDtoResponse> getAllUserTasks(UUID userId) {
        return taskDAO.findAllTasksByCustomerId(userId).stream()
                .map(this::enrichByUsersInfo)
                .collect(Collectors.toList());
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public TaskDtoResponse getTaskById(UUID id) {
        TaskDtoResponse taskDtoResponse = null;
        Optional<Task> optionalTask = taskDAO.findTaskById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            taskDtoResponse = enrichByUsersInfo(task);
        }
        return taskDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TaskDtoResponse createTask(TaskDtoRequest taskDtoRequest) {
        TaskDtoResponse taskDtoResponse = null;
        Optional<User> userOptional = userDAO.findUserById(taskDtoRequest.getCustomerId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            checkUserPermissions(user);
            userDAO.updateUserStatus(user, ACTIVE);
            Task task = taskMapper.requestDtoToEntity(taskDtoRequest);
            task.setCustomer(user);
            taskDAO.save(task);
            taskDtoResponse = enrichByUsersInfo(task);
        }
        return taskDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TaskDtoResponse updateTask(TaskDtoRequest taskDtoRequest) {
        TaskDtoResponse taskDtoResponse = null;
        if (taskDtoRequest.getUserId() != null
                && taskDAO.taskWithIdIsExist(taskDtoRequest.getUserId())) {

            Optional<User> userOptional = userDAO.findUserById(taskDtoRequest.getUserId());
            Optional<Task> taskById = taskDAO.findTaskById(taskDtoRequest.getUserId());
            if (userOptional.isPresent() && taskById.isPresent()) {
                User user = userOptional.get();
                Task task = taskById.get();
                if (allowToUpdate(user, task)) {
                    taskMapper.requestDtoToEntity(taskDtoRequest, task, user.getRole().getValue());
                    taskDAO.update(task);
                    taskDtoResponse = enrichByUsersInfo(task);
                }
            }
        }
        return taskDtoResponse;
    }


    /**
     * SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     * REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     * должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteTask(UUID id) {
        Optional<Task> optionalTask = taskDAO.findTaskById(id);
        if (optionalTask.isPresent()) {
            Optional<Contract> optionalContract = contractDAO.findContractByTask(optionalTask.get());
            optionalContract.ifPresent(contractDAO::delete);
            taskDAO.deleteByPK(id);
            return true;
        }
        return false;
    }


    /**
     * Метод формирует ответ TaskDtoResponse и обогащает его данными о заказчике и исполнителе
     *
     * @param task - объект задания
     * @return - объект TaskDtoResponse с подготовленными данными о задании, исполнителе и заказчике
     */
    @Override
    public TaskDtoResponse enrichByUsersInfo(Task task) {
        TaskDtoResponse taskDtoResponse = taskMapper.entityToResponseDto(task);
        taskDtoResponse.setCustomer(userMapper.entityToResponseDto(task.getCustomer()));
        if (task.getExecutor() != null) {
            taskDtoResponse.setExecutor(userMapper.entityToResponseDto(task.getExecutor()));
        }
        return taskDtoResponse;
    }


    public TaskDAO getTaskDAO() {
        return taskDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public TaskDtoEntityMapper getTaskMapper() {
        return taskMapper;
    }

    public UserDtoEntityMapper getUserMapper() {
        return userMapper;
    }


    /**
     * Метод проверяет возможность обновления контракта
     *
     * @param user - пользователь инициировавший процесс
     * @param task - задание
     * @return - true - пользователь не заблокирован, роль пользователя позволяет менять статаус задания,
     * false - в любом ином случае
     */
    private boolean allowToUpdate(User user, Task task) {
        boolean userNotBlocked = !isEqualsUserStatus(BLOCKED, user);
        boolean userIsCustomerAndTaskOnCustomer = user.getId().equals(task.getCustomer().getId()) &&
                (isEqualsTaskStatus(REGISTERED, task) || isEqualsTaskStatus(ON_CHECK, task));
        boolean userIsExecutorAndTaskOnExecutor = (task.getExecutor() != null) &&
                user.getId().equals(task.getExecutor().getId()) &&
                (isEqualsTaskStatus(IN_PROGRESS, task) || isEqualsTaskStatus(ON_FIX, task));

        return userNotBlocked && (userIsCustomerAndTaskOnCustomer || userIsExecutorAndTaskOnExecutor);
    }


    /**
     * Метод проверяет доступна ли для пользователя операция создания задания
     *
     * @param user - сущность пользователя
     */
    private void checkUserPermissions(User user) {
        if (isEqualsUserRole(EXECUTOR, user) || isEqualsUserStatus(BLOCKED, user)) {
            throw new UnavailableRoleOperationException(unavailableOperationMessage);
        }
    }
}
