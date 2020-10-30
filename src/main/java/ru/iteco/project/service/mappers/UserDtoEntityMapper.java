package ru.iteco.project.service.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.TaskDtoResponse;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.model.Task;
import ru.iteco.project.model.User;
import ru.iteco.project.service.TaskServiceImpl;

import java.util.UUID;

@Service
public class UserDtoEntityMapper implements DtoEntityMapper<User, UserDtoRequest, UserDtoResponse> {

    TaskServiceImpl taskService;

    @Autowired
    @Lazy
    public UserDtoEntityMapper(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    public UserDtoEntityMapper() {
    }

    @Override
    public UserDtoResponse entityToResponseDto(User entity) {
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        if (entity != null) {
            userDtoResponse.setId(entity.getId());
            userDtoResponse.setFirstName(entity.getFirstName());
            userDtoResponse.setSecondName(entity.getSecondName());
            userDtoResponse.setLastName(entity.getLastName());
            userDtoResponse.setEmail(entity.getEmail());
            userDtoResponse.setPhoneNumber(entity.getPhoneNumber());
            userDtoResponse.setLogin(entity.getLogin());
            userDtoResponse.setRole(entity.getRole());
            userDtoResponse.setUserStatus(entity.getUserStatus());
            userDtoResponse.setWallet(entity.getWallet());
        }
        return userDtoResponse;
    }

    @Override
    public User requestDtoToEntity(UserDtoRequest requestDto) {
        User user = new User();
        if (requestDto != null) {
            user.setId(UUID.randomUUID());
            user.setFirstName(requestDto.getFirstName());
            user.setSecondName(requestDto.getSecondName());
            user.setLastName(requestDto.getLastName());
            user.setLogin(requestDto.getLogin());
            user.setPassword(requestDto.getPassword());
            user.setEmail(requestDto.getEmail());
            user.setPhoneNumber(requestDto.getPhoneNumber());
            user.setRole(requestDto.getRole());
            user.setUserStatus(requestDto.getUserStatus());
            user.setWallet(requestDto.getWallet());
        }
        return user;
    }

    public TaskServiceImpl getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }
}
