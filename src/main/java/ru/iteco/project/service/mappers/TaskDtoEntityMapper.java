package ru.iteco.project.service.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.TaskDtoRequest;
import ru.iteco.project.controller.dto.TaskDtoResponse;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.model.Task;
import ru.iteco.project.model.TaskStatus;
import ru.iteco.project.model.User;
import ru.iteco.project.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class TaskDtoEntityMapper implements DtoEntityMapper<Task, TaskDtoRequest, TaskDtoResponse> {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";


    private UserServiceImpl userService;

    @Autowired
    @Lazy
    public TaskDtoEntityMapper(UserServiceImpl userService) {
        this.userService = userService;
    }

    public TaskDtoEntityMapper() {
    }

    @Override
    public TaskDtoResponse entityToResponseDto(Task entity) {
        TaskDtoResponse taskDtoResponse = new TaskDtoResponse();
        if (entity != null) {
            taskDtoResponse.setId(entity.getId());
            taskDtoResponse.setName(entity.getName());
            taskDtoResponse.setDescription(entity.getDescription());
            taskDtoResponse.setTaskCreationDate(entity.getTaskCreationDate());
            taskDtoResponse.setTaskCompletionDate(entity.getTaskCompletionDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            taskDtoResponse.setPrice(entity.getPrice());
            taskDtoResponse.setTaskStatus(entity.getTaskStatus().name());
            taskDtoResponse.setTaskDecision(entity.getTaskDecision());

            taskDtoResponse.setCustomer(userService.getUserById(entity.getCustomerId()));
            taskDtoResponse.setExecutor(userService.getUserById(entity.getExecutorId()));
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
            task.setTaskCompletionDate(LocalDateTime.parse(requestDto.getTaskCompletionDate(), DateTimeFormatter.ofPattern(DATE_FORMAT)));
            task.setPrice(requestDto.getPrice());
            task.setTaskStatus(TaskStatus.TASK_REGISTERED);
            task.setCustomerId(requestDto.getCustomerId());
        }
        return task;
    }

    public UserServiceImpl getUserService() {
        return userService;
    }

    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }
}
