package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.controller.dto.TaskStatusDtoRequest;
import ru.iteco.project.controller.dto.TaskStatusDtoResponse;
import ru.iteco.project.dao.TaskDAO;
import ru.iteco.project.dao.TaskStatusDAO;
import ru.iteco.project.dao.UserDAO;
import ru.iteco.project.model.*;
import ru.iteco.project.service.mappers.TaskStatusDtoEntityMapper;

import java.util.*;

import static ru.iteco.project.model.TaskStatus.TaskStatusEnum.isEqualsTaskStatus;
import static ru.iteco.project.model.UserRole.UserRoleEnum.ADMIN;
import static ru.iteco.project.model.UserRole.UserRoleEnum.isEqualsUserRole;

/**
 * Класс реализует функционал сервисного слоя для работы со статусами заданий
 */
@Service
public class TaskStatusServiceImpl implements TaskStatusService {

    private static final Logger log = LogManager.getLogger(TaskStatusServiceImpl.class.getName());


    private final TaskStatusDAO taskStatusDAO;
    private final TaskDAO taskDAO;
    private final UserDAO userDAO;
    private final TaskService taskService;
    private final TaskStatusDtoEntityMapper taskStatusDtoEntityMapper;


    public TaskStatusServiceImpl(TaskStatusDAO taskStatusDAO, TaskDAO taskDAO, UserDAO userDAO, TaskService taskService,
                                 TaskStatusDtoEntityMapper taskStatusDtoEntityMapper) {
        this.taskStatusDAO = taskStatusDAO;
        this.taskDAO = taskDAO;
        this.userDAO = userDAO;
        this.taskService = taskService;
        this.taskStatusDtoEntityMapper = taskStatusDtoEntityMapper;
    }


    /**
     *  По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     *  REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(readOnly = true)
    public TaskStatusDtoResponse getTaskStatusById(UUID id) {
        TaskStatusDtoResponse taskStatusDtoResponse = new TaskStatusDtoResponse();
        Optional<TaskStatus> optionalTaskStatusById = taskStatusDAO.findTaskStatusById(id);
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
            taskStatusDAO.save(newTaskStatus);
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
                taskStatusDAO.taskStatusWithIdIsExist(taskStatusDtoRequest.getId())) {

            TaskStatus taskStatus = taskStatusDtoEntityMapper.requestDtoToEntity(taskStatusDtoRequest);
            taskStatus.setId(id);
            taskStatusDAO.update(taskStatus);
            taskStatusDtoResponse = taskStatusDtoEntityMapper.entityToResponseDto(taskStatus);
        }
        return taskStatusDtoResponse;
    }

    /**
     *  По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public ArrayList<TaskStatusDtoResponse> getAllTasksStatuses() {
        ArrayList<TaskStatusDtoResponse> taskStatusDtoResponseList = new ArrayList<>();
        for (TaskStatus userStatus : taskStatusDAO.getAll()) {
            taskStatusDtoResponseList.add(taskStatusDtoEntityMapper.entityToResponseDto(userStatus));
        }
        return taskStatusDtoResponseList;
    }

    /**
     *  SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     *  REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     *  должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteTaskStatus(UUID id) {
        Optional<TaskStatus> taskStatusById = taskStatusDAO.findTaskStatusById(id);
        if (taskStatusById.isPresent()) {
            Collection<Task> allTasksWithStatus = taskDAO.findAllTasksWithStatus(taskStatusById.get());
            allTasksWithStatus.forEach(task -> taskService.deleteTask(task.getId()));
            taskStatusDAO.deleteByPK(id);
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
            Optional<User> userById = userDAO.findUserById(taskStatusDtoRequest.getUserId());
            if (userById.isPresent()) {
                return isEqualsUserRole(ADMIN, userById.get());
            }
        }
        return false;
    }
}
