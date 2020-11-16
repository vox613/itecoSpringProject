package ru.iteco.project.service.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.TaskDtoRequest;
import ru.iteco.project.controller.dto.TaskDtoResponse;
import ru.iteco.project.dao.UserDAO;
import ru.iteco.project.model.Role;
import ru.iteco.project.model.Task;
import ru.iteco.project.model.TaskStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Класс маппер для сущности Task
 */
@Service
@PropertySource(value = {"classpath:application.properties"})
public class TaskDtoEntityMapper implements DtoEntityMapper<Task, TaskDtoRequest, TaskDtoResponse> {

    /**
     * Объект доступа к DAO слою Пользователей
     */
    private final UserDAO userDAO;

    /*** Установленный формат даты и времени*/
    @Value("${format.date.time}")
    private String formatDateTime;

    @Autowired
    public TaskDtoEntityMapper(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public TaskDtoResponse entityToResponseDto(Task entity) {
        TaskDtoResponse taskDtoResponse = new TaskDtoResponse();
        if (entity != null) {
            taskDtoResponse.setId(entity.getId());
            taskDtoResponse.setCustomerId(entity.getCustomer().getId());
            taskDtoResponse.setName(entity.getName());
            taskDtoResponse.setDescription(entity.getDescription());
            taskDtoResponse.setTaskCreationDate(entity.getTaskCreationDate().format(DateTimeFormatter.ofPattern(formatDateTime)));
            taskDtoResponse.setTaskCompletionDate(entity.getTaskCompletionDate().format(DateTimeFormatter.ofPattern(formatDateTime)));
            taskDtoResponse.setLastTaskUpdateDate(entity.getLastTaskUpdateDate().format(DateTimeFormatter.ofPattern(formatDateTime)));
            taskDtoResponse.setPrice(entity.getPrice());
            taskDtoResponse.setTaskStatus(entity.getTaskStatus());
            taskDtoResponse.setTaskDecision(entity.getTaskDecision());
            if (entity.getExecutor() != null) {
                taskDtoResponse.setExecutorId(entity.getCustomer().getId());
            }
        }
        return taskDtoResponse;
    }


    @Override
    public Task requestDtoToEntity(TaskDtoRequest requestDto) {
        Task task = new Task();
        if (requestDto != null) {
            task.setId(UUID.randomUUID());
            task.setName(requestDto.getName());
            task.setDescription(requestDto.getDescription());
            task.setTaskCompletionDate(LocalDateTime.parse(requestDto.getTaskCompletionDate(), DateTimeFormatter.ofPattern(formatDateTime)));
            task.setPrice(requestDto.getPrice());
            task.setTaskStatus(TaskStatus.TASK_REGISTERED);
            task.setCustomer(userDAO.findUserById(requestDto.getCustomerId()).orElse(null));
            task.setLastTaskUpdateDate(LocalDateTime.now());
            task.setTaskDecision(requestDto.getTaskDecision());
        }
        return task;
    }

    /**
     * Метод обновляет данными запроса информацию в Задании в зависимости от роли пользователя, инициировавщего процесс
     *
     * @param requestDto - запрос с данными для обновления
     * @param task       - сущность Задания для обновления
     * @param role       - роль пользователя, инициировавщего процесс
     */
    public void requestDtoToEntity(TaskDtoRequest requestDto, Task task, Role role) {
        if (requestDto != null) {
            if (Role.CUSTOMER.equals(role)) {
                task.setName(requestDto.getName());
                task.setDescription(requestDto.getDescription());
                task.setTaskCompletionDate(LocalDateTime.parse(requestDto.getTaskCompletionDate(), DateTimeFormatter.ofPattern(formatDateTime)));
                task.setPrice(requestDto.getPrice());
                if (requestDto.getTaskStatus() != null) {
                    task.setTaskStatus(TaskStatus.valueOf(requestDto.getTaskStatus()));
                }
            } else if (Role.EXECUTOR.equals(role)) {
                task.setTaskDecision(requestDto.getTaskDecision());
                task.setTaskStatus(TaskStatus.TASK_ON_CHECK);
            }
            task.setLastTaskUpdateDate(LocalDateTime.now());
        }
    }
}
