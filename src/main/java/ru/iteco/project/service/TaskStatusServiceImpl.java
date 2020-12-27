package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.controller.dto.TaskStatusDtoRequest;
import ru.iteco.project.controller.dto.TaskStatusDtoResponse;
import ru.iteco.project.dao.TaskRepository;
import ru.iteco.project.dao.TaskStatusRepository;
import ru.iteco.project.dao.UserRepository;
import ru.iteco.project.domain.Task;
import ru.iteco.project.domain.TaskStatus;
import ru.iteco.project.domain.User;
import ru.iteco.project.service.mappers.TaskStatusDtoEntityMapper;

import java.util.*;

import static ru.iteco.project.domain.UserRole.UserRoleEnum.ADMIN;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.isEqualsUserRole;

/**
 * Класс реализует функционал сервисного слоя для работы со статусами заданий
 */
@Service
public class TaskStatusServiceImpl implements TaskStatusService {

    private static final Logger log = LogManager.getLogger(TaskStatusServiceImpl.class.getName());


    /*** Объект доступа к репозиторию статусов заданий */
    private final TaskStatusRepository taskStatusRepository;

    /*** Объект доступа к репозиторию заданий */
    private final TaskRepository taskRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final UserRepository userRepository;

    /*** Объект сервисного слоя заданий */
    private final TaskService taskService;

    /*** Объект маппера dto статуса задания в сущность статуса задания */
    private final TaskStatusDtoEntityMapper taskStatusDtoEntityMapper;


    public TaskStatusServiceImpl(TaskStatusRepository taskStatusRepository, TaskRepository taskRepository, UserRepository userRepository,
                                 TaskService taskService, TaskStatusDtoEntityMapper taskStatusDtoEntityMapper) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskService = taskService;
        this.taskStatusDtoEntityMapper = taskStatusDtoEntityMapper;
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(readOnly = true)
    public TaskStatusDtoResponse getTaskStatusById(UUID id) {
        TaskStatusDtoResponse taskStatusDtoResponse = new TaskStatusDtoResponse();
        Optional<TaskStatus> optionalTaskStatusById = taskStatusRepository.findById(id);
        if (optionalTaskStatusById.isPresent()) {
            TaskStatus taskStatus = optionalTaskStatusById.get();
            taskStatusDtoResponse = taskStatusDtoEntityMapper.entityToResponseDto(taskStatus);
        }
        return taskStatusDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TaskStatusDtoResponse createTaskStatus(TaskStatusDtoRequest taskStatusDtoRequest) {
        TaskStatusDtoResponse taskStatusDtoResponse = new TaskStatusDtoResponse();
        if (operationIsAllow(taskStatusDtoRequest)) {
            TaskStatus newTaskStatus = taskStatusDtoEntityMapper.requestDtoToEntity(taskStatusDtoRequest);
            taskStatusRepository.save(newTaskStatus);
            taskStatusDtoResponse = taskStatusDtoEntityMapper.entityToResponseDto(newTaskStatus);
        }
        return taskStatusDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TaskStatusDtoResponse updateTaskStatus(UUID id, TaskStatusDtoRequest taskStatusDtoRequest) {
        TaskStatusDtoResponse taskStatusDtoResponse = new TaskStatusDtoResponse();
        if (operationIsAllow(taskStatusDtoRequest) &&
                Objects.equals(id, taskStatusDtoRequest.getId()) &&
                taskStatusRepository.existsById(taskStatusDtoRequest.getId())) {

            TaskStatus taskStatus = taskStatusDtoEntityMapper.requestDtoToEntity(taskStatusDtoRequest);
            taskStatus.setId(id);
            taskStatusRepository.save(taskStatus);
            taskStatusDtoResponse = taskStatusDtoEntityMapper.entityToResponseDto(taskStatus);
        }
        return taskStatusDtoResponse;
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public ArrayList<TaskStatusDtoResponse> getAllTasksStatuses() {
        ArrayList<TaskStatusDtoResponse> taskStatusDtoResponseList = new ArrayList<>();
        for (TaskStatus userStatus : taskStatusRepository.findAll()) {
            taskStatusDtoResponseList.add(taskStatusDtoEntityMapper.entityToResponseDto(userStatus));
        }
        return taskStatusDtoResponseList;
    }

    /**
     * SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     * REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     * должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteTaskStatus(UUID id) {
        Optional<TaskStatus> taskStatusById = taskStatusRepository.findById(id);
        if (taskStatusById.isPresent()) {
            Collection<Task> allTasksWithStatus = taskRepository.findAllByTaskStatus(taskStatusById.get());
            allTasksWithStatus.forEach(task -> taskService.deleteTask(task.getId()));
            taskStatusRepository.deleteById(id);
            return true;
        }
        return false;
    }


    /**
     * Метод проверяет разрешена ли для пользователя данная операция
     *
     * @param taskStatusDtoRequest - запрос
     * @return true - операция разрешена, false - операция запрещена
     */
    private boolean operationIsAllow(TaskStatusDtoRequest taskStatusDtoRequest) {
        if ((taskStatusDtoRequest != null) && (taskStatusDtoRequest.getUserId() != null)) {
            Optional<User> userById = userRepository.findById(taskStatusDtoRequest.getUserId());
            if (userById.isPresent()) {
                return isEqualsUserRole(ADMIN, userById.get());
            }
        }
        return false;
    }
}
