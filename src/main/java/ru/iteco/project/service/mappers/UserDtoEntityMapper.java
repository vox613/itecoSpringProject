package ru.iteco.project.service.mappers;

import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.dao.TaskDAO;
import ru.iteco.project.model.Role;
import ru.iteco.project.model.User;
import ru.iteco.project.model.UserStatus;

import java.util.UUID;

/**
 * Класс маппер для сущности User
 */
@Service
public class UserDtoEntityMapper implements DtoEntityMapper<User, UserDtoRequest, UserDtoResponse> {

    /**
     * Объект доступа к DAO слою Заданий
     */
    private final TaskDAO taskDAO;


    public UserDtoEntityMapper(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    @Override
    public UserDtoResponse entityToResponseDto(User entity) {
        final UserDtoResponse userDtoResponse = new UserDtoResponse();
        if (entity != null) {
            userDtoResponse.setId(entity.getId());
            userDtoResponse.setFirstName(entity.getFirstName());
            userDtoResponse.setSecondName(entity.getSecondName());
            userDtoResponse.setLastName(entity.getLastName());
            userDtoResponse.setEmail(entity.getEmail());
            userDtoResponse.setPhoneNumber(entity.getPhoneNumber());
            userDtoResponse.setLogin(entity.getLogin());
            userDtoResponse.setRole(entity.getRole().name());
            userDtoResponse.setUserStatus(entity.getUserStatus().name());
            userDtoResponse.setWallet(entity.getWallet());
            if (Role.CUSTOMER.equals(entity.getRole())) {
                taskDAO.findAllTasksByCustomer(entity)
                        .forEach(task -> userDtoResponse.getTasksIdList().add(task.getId()));
            } else if (Role.EXECUTOR.equals(entity.getRole())) {
                taskDAO.findAllTasksByExecutor(entity)
                        .forEach(task -> userDtoResponse.getTasksIdList().add(task.getId()));
            }
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
            user.setRole(Role.valueOf(requestDto.getRole()));
            if (requestDto.getUserStatus() != null) {
                user.setUserStatus(UserStatus.valueOf(requestDto.getUserStatus()));
            }
            user.setWallet(requestDto.getWallet());
        }
        return user;
    }

}
