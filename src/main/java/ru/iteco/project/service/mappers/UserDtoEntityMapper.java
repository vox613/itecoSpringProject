package ru.iteco.project.service.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.dao.TaskDAO;
import ru.iteco.project.dao.UserRoleDAO;
import ru.iteco.project.dao.UserStatusDAO;
import ru.iteco.project.exception.InvalidUserRoleException;
import ru.iteco.project.exception.InvalidUserStatusException;
import ru.iteco.project.model.User;

import java.util.UUID;

/**
 * Класс маппер для сущности User
 */
@Service
@PropertySource(value = {"classpath:errors.properties"})
public class UserDtoEntityMapper implements DtoEntityMapper<User, UserDtoRequest, UserDtoResponse> {

    /*** Объект доступа к DAO слою Заданий*/
    private final TaskDAO taskDAO;

    /*** Объект доступа к DAO слою ролей пользователей*/
    private final UserRoleDAO userRoleDAO;

    /*** Объект доступа к DAO слою статусов пользователей*/
    private final UserStatusDAO userStatusDAO;

    @Value("${errors.user.role.operation.unavailable}")
    private String unavailableOperationMessage;

    @Value("${errors.user.role.invalid}")
    private String userRoleIsInvalidMessage;


    public UserDtoEntityMapper(TaskDAO taskDAO, UserRoleDAO userRoleDAO, UserStatusDAO userStatusDAO) {
        this.taskDAO = taskDAO;
        this.userRoleDAO = userRoleDAO;
        this.userStatusDAO = userStatusDAO;
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
            userDtoResponse.setRole(entity.getRole().getValue());
            userDtoResponse.setUserStatus(entity.getUserStatus().getValue());
            userDtoResponse.setWallet(entity.getWallet());
            taskDAO.findAllTasksByUser(entity)
                    .forEach(task -> userDtoResponse.getTasksIdList().add(task.getId()));
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
            user.setRole(userRoleDAO.findUserRoleByValue(requestDto.getRole())
                    .orElseThrow(() -> new InvalidUserRoleException(userRoleIsInvalidMessage)));
            user.setUserStatus(userStatusDAO.findUserStatusByValue(requestDto.getUserStatus())
                    .orElseThrow(() -> new InvalidUserStatusException(unavailableOperationMessage)));
            user.setWallet(requestDto.getWallet());
        }
        return user;
    }

    public TaskDAO getTaskDAO() {
        return taskDAO;
    }

    public UserRoleDAO getUserRoleDAO() {
        return userRoleDAO;
    }

    public UserStatusDAO getUserStatusDAO() {
        return userStatusDAO;
    }
}
