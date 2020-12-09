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
import ru.iteco.project.dao.ContractRepository;
import ru.iteco.project.dao.TaskRepository;
import ru.iteco.project.dao.UserRepository;
import ru.iteco.project.exception.UnavailableRoleOperationException;
import ru.iteco.project.domain.Contract;
import ru.iteco.project.domain.Task;
import ru.iteco.project.domain.User;
import ru.iteco.project.service.mappers.TaskDtoEntityMapper;
import ru.iteco.project.service.mappers.UserDtoEntityMapper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.*;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.EXECUTOR;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.isEqualsUserRole;
import static ru.iteco.project.domain.UserStatus.UserStatusEnum.*;

/**
 * Класс реализует функционал сервисного слоя для работы с заданиями
 */
@Service
@PropertySource(value = {"classpath:errors.properties"})
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LogManager.getLogger(TaskServiceImpl.class.getName());

    @Value("${errors.user.role.operation.unavailable}")
    private String unavailableOperationMessage;

    /*** Объект доступа к репозиторию заданий */
    private final TaskRepository taskRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final UserRepository userRepository;

    /*** Объект доступа к репозиторию контрактов */
    private final ContractRepository contractRepository;

    /*** Объект маппера dto задания в сущность задания */
    private final TaskDtoEntityMapper taskMapper;

    /*** Объект маппера dto пользователя в сущность пользователя */
    private final UserDtoEntityMapper userMapper;


    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, ContractRepository contractRepository,
                           TaskDtoEntityMapper taskMapper, UserDtoEntityMapper userMapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
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
        for (Task task : taskRepository.findAll()) {
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
        return taskRepository.findTasksByCustomerId(userId).stream()
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
        Optional<Task> optionalTask = taskRepository.findById(id);
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
        Optional<User> userOptional = userRepository.findById(taskDtoRequest.getCustomerId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            checkUserPermissions(user);
            userMapper.updateUserStatus(user, ACTIVE);
            Task task = taskMapper.requestDtoToEntity(taskDtoRequest);
            task.setCustomer(user);
            taskRepository.save(task);
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
    public TaskDtoResponse updateTask(UUID id, TaskDtoRequest taskDtoRequest) {
        TaskDtoResponse taskDtoResponse = null;
        if (Objects.equals(id, taskDtoRequest.getId())
                && taskDtoRequest.getUserId() != null
                && taskRepository.existsById(id)) {

            Optional<User> userOptional = userRepository.findById(taskDtoRequest.getUserId());
            Optional<Task> taskById = taskRepository.findById(id);
            if (userOptional.isPresent() && taskById.isPresent()) {
                User user = userOptional.get();
                Task task = taskById.get();
                if (allowToUpdate(user, task)) {
                    taskMapper.requestDtoToEntity(taskDtoRequest, task, user.getRole().getValue());
                    taskRepository.save(task);
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
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Optional<Contract> optionalContract = contractRepository.findContractByTask(optionalTask.get());
            optionalContract.ifPresent(contractRepository::delete);
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }


    @Override
    public TaskDtoResponse enrichByUsersInfo(Task task) {
        TaskDtoResponse taskDtoResponse = taskMapper.entityToResponseDto(task);
        taskDtoResponse.setCustomer(userMapper.entityToResponseDto(task.getCustomer()));
        if (task.getExecutor() != null) {
            taskDtoResponse.setExecutor(userMapper.entityToResponseDto(task.getExecutor()));
        }
        return taskDtoResponse;
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
