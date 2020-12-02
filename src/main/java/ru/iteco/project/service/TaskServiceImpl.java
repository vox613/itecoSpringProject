package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.TaskDtoRequest;
import ru.iteco.project.controller.dto.TaskDtoResponse;
import ru.iteco.project.dao.ContractDAO;
import ru.iteco.project.dao.TaskDAO;
import ru.iteco.project.dao.UserDAO;
import ru.iteco.project.exception.UnavailableRoleOperationException;
import ru.iteco.project.model.*;
import ru.iteco.project.service.mappers.TaskDtoEntityMapper;
import ru.iteco.project.service.mappers.UserDtoEntityMapper;
import ru.iteco.project.service.validators.CustomValidator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final CustomValidator taskValidator;
    private final TaskDtoEntityMapper taskMapper;
    private final UserDtoEntityMapper userMapper;


    public TaskServiceImpl(TaskDAO taskDAO, UserDAO userDAO, ContractDAO contractDAO, CustomValidator taskValidator,
                           TaskDtoEntityMapper taskMapper, UserDtoEntityMapper userMapper) {
        this.taskDAO = taskDAO;
        this.userDAO = userDAO;
        this.contractDAO = contractDAO;
        this.taskValidator = taskValidator;
        this.taskMapper = taskMapper;
        this.userMapper = userMapper;
    }

    /**
     * Метод сохранения задания в коллекцию
     *
     * @param task - задание для сохраннения
     */
    @Override
    public void createTask(Task task) {
        taskValidator.validate(task);
        taskDAO.save(task);
        log.info("now: " + LocalDateTime.now() + " createTask: " + task);
    }

    /**
     * Метод поиска задания по названию
     *
     * @param name - название задания
     * @return - список заданий, название которых совпадает с переданным
     */
    @Override
    public List<Task> findTaskByName(String name) {
        List<Task> taskList = taskDAO.findTaskByName(name);
        log.info("now: " + LocalDateTime.now() + " findTaskByName: " + taskList);
        return taskList;
    }

    /**
     * Метод удаления из коллекции переданного задания
     *
     * @param task - задание для удаления
     * @return - удаленное задание
     */
    @Override
    public Task deleteTask(Task task) {
        Task deletedTask = taskDAO.delete(task);
        log.info("now: " + LocalDateTime.now() + " deleteTask: " + deletedTask);
        return deletedTask;
    }

    /**
     * Метод изменения статуса задания на переданный в агументах
     *
     * @param task       - задание статус которого необходимо изменить
     * @param taskStatus - статус на которой меняется состояние задания
     */
    @Override
    public void changeTaskStatusTo(Task task, TaskStatus taskStatus) {
        task.setTaskStatus(taskStatus);
        taskDAO.update(task);
        log.info("now: " + LocalDateTime.now() + " changeTaskStatusTo: " + task + "StatusTo: " + taskStatus);
    }

    /**
     * Метод получает все задания из коллекции
     *
     * @return - список всех заданий из коллекции
     */
    @Override
    public List<TaskDtoResponse> getAllTasks() {
        ArrayList<TaskDtoResponse> taskDtoResponses = new ArrayList<>();
        for (Task task : taskDAO.getAll()) {
            taskDtoResponses.add(getTaskById(task.getId()));
        }
        return taskDtoResponses;
    }

    @Override
    public List<TaskDtoResponse> getAllUserTasks(UUID userId) {
        return taskDAO.findAllTasksByCustomerId(userId).stream()
                .map(task -> getTaskById(task.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public TaskDtoResponse getTaskById(UUID id) {
        TaskDtoResponse taskDtoResponse = new TaskDtoResponse();
        Optional<Task> optionalTask = taskDAO.findTaskById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            taskDtoResponse = taskMapper.entityToResponseDto(task);
            taskDtoResponse.setCustomer(userMapper.entityToResponseDto(task.getCustomer()));
            if (task.getExecutor() != null) {
                taskDtoResponse.setExecutor(userMapper.entityToResponseDto(task.getExecutor()));
            }
        }
        return taskDtoResponse;
    }


    @Override
    public TaskDtoRequest createTask(TaskDtoRequest taskDtoRequest) {
        Optional<User> userOptional = userDAO.findUserById(taskDtoRequest.getCustomerId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            checkUserPermissions(user);
            user.setUserStatus(UserStatus.STATUS_ACTIVE);
            userDAO.save(user);
            Task task = taskMapper.requestDtoToEntity(taskDtoRequest);
            taskDAO.save(task);
            taskDtoRequest.setId(task.getId());
            taskDtoRequest.setTaskStatus(task.getTaskStatus().name());
            return taskDtoRequest;
        }
        return taskDtoRequest;
    }


    @Override
    public TaskDtoResponse updateTask(UUID id, TaskDtoRequest taskDtoRequest) {
        TaskDtoResponse taskDtoResponse = null;
        if (Objects.equals(id, taskDtoRequest.getId())
                && taskDtoRequest.getUserId() != null
                && taskDAO.taskWithIdIsExist(id)) {

            Optional<User> userOptional = userDAO.findUserById(taskDtoRequest.getUserId());
            Optional<Task> taskById = taskDAO.findTaskById(id);
            if (userOptional.isPresent() && taskById.isPresent()) {
                User user = userOptional.get();
                Task task = taskById.get();
                if (allowToUpdate(user, task)) {
                    taskMapper.requestDtoToEntity(taskDtoRequest, task, user.getRole());
                    taskDAO.update(task);
                    taskDtoResponse = enrichByUsersInfo(task);
                }
            }
        }
        return taskDtoResponse;
    }

    @Override
    public Boolean deleteTask(UUID id) {
        Optional<Task> taskById = taskDAO.findTaskById(id);
        if (taskById.isPresent()) {
            Contract contractByTask = contractDAO.findContractByTask(taskById.get());
            if (contractByTask != null){
                contractDAO.delete(contractByTask);
            }
            taskDAO.deleteByPK(id);
            return true;
        }
        return false;
    }

    public TaskDAO getTaskDAO() {
        return taskDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public CustomValidator getTaskValidator() {
        return taskValidator;
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
        boolean userNotBlocked = !UserStatus.STATUS_BLOCKED.equals(user.getUserStatus());
        boolean userIsCustomerAndTaskRegistered = user.getId().equals(task.getCustomer().getId()) &&
                (TaskStatus.TASK_REGISTERED.equals(task.getTaskStatus()) || TaskStatus.TASK_ON_CHECK.equals(task.getTaskStatus()));
        boolean userIsExecutorAndTaskInProgress = (task.getExecutor() != null) &&
                user.getId().equals(task.getExecutor().getId()) &&
                (TaskStatus.TASK_IN_PROGRESS.equals(task.getTaskStatus()) || TaskStatus.TASK_ON_FIX.equals(task.getTaskStatus()));

        return userNotBlocked && (userIsCustomerAndTaskRegistered || userIsExecutorAndTaskInProgress);
    }

    /**
     * Метод проверяет доступна ли для пользователя операция создания задания
     *
     * @param user - сущность пользователя
     */
    private void checkUserPermissions(User user) {
        if (Role.ROLE_EXECUTOR.equals(user.getRole()) || UserStatus.STATUS_BLOCKED.equals(user.getUserStatus())) {
            throw new UnavailableRoleOperationException(unavailableOperationMessage);
        }
    }

    /**
     * Метод формирует ответ TaskDtoResponse и обогащает его данными о заказчике и исполнителе
     *
     * @param task - объект задания
     * @return - объект TaskDtoResponse с подготовленными данными о задании, исполнителе и заказчике
     */
    public TaskDtoResponse enrichByUsersInfo(Task task) {
        TaskDtoResponse taskDtoResponse = taskMapper.entityToResponseDto(task);
        taskDtoResponse.setCustomer(userMapper.entityToResponseDto(task.getCustomer()));
        if (task.getExecutor() != null) {
            taskDtoResponse.setExecutor(userMapper.entityToResponseDto(task.getExecutor()));
        }
        return taskDtoResponse;
    }
}
