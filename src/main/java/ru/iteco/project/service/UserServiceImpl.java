package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.dao.TaskDAO;
import ru.iteco.project.dao.UserDAO;
import ru.iteco.project.model.User;
import ru.iteco.project.model.UserStatus;
import ru.iteco.project.service.mappers.UserDtoEntityMapper;
import ru.iteco.project.service.validators.CustomValidator;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс реализует функционал сервисного слоя для работы с пользователями
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LogManager.getLogger(UserServiceImpl.class.getName());

    private final UserDAO userDAO;
    private final TaskDAO taskDAO;
    private final CustomValidator userValidator;
    private final UserDtoEntityMapper userMapper;


    @Autowired
    public UserServiceImpl(UserDAO userDAO, TaskDAO taskDAO, CustomValidator userValidator,
                           UserDtoEntityMapper userMapper) {
        this.userDAO = userDAO;
        this.taskDAO = taskDAO;
        this.userValidator = userValidator;
        this.userMapper = userMapper;
    }


    /**
     * Метод сохранения пользователя в коллекцию
     *
     * @param user - пользователь для сохраннения
     */
    @Override
    public void addUser(User user) {
        userValidator.validate(user);
        log.info("now: " + LocalDateTime.now() + " addUser: " + user);
        userDAO.save(user);
    }

    /**
     * Метод получения пользователя по логину
     *
     * @param login - логин пользователя
     * @return - объект пользователя, у которого логин соответствует переданному или null,
     * если такой пользователь не был найден
     */
    @Override
    public User getUserByLogin(String login) {
        User userByLogin = userDAO.findUserByLogin(login);
        log.info("now: " + LocalDateTime.now() + " ByLogin: " + login + " get User: " + userByLogin);
        return userByLogin;
    }

    /**
     * Метод довавляет в коллекцию переданный список пользователей, перезаписывая информацию о тех,
     * которые уже присутствуют в коллекции
     *
     * @param userList - список пользователей для добавления в коллекцию
     */
    @Override
    public void addAllUsers(List<User> userList) {
        log.info("now: " + LocalDateTime.now() + " addAllUsers: " + userList.toString());
        userDAO.addAll(userList);
    }

    /**
     * Метод удаляет пользователя из коллекции
     *
     * @param user - пользователь для удаления
     * @return - удаленный пользователь
     */
    @Override
    public User deleteUser(User user) {
        log.info("now: " + LocalDateTime.now() + " deleteUser: " + user);
        return userDAO.delete(user);
    }

    /**
     * Метод изменения статуса пользователя на переданный в агументах
     *
     * @param user       - пользователь статус которого необходимо изменить
     * @param userStatus - статус на которой меняется состояние пользователя
     */
    @Override
    public void changeUserStatusTo(User user, UserStatus userStatus) {
        user.setUserStatus(userStatus);
        userDAO.update(user);
        log.info("now: " + LocalDateTime.now() + " changeUser: " + user + "StatusTo: " + userStatus);
    }

    /**
     * Метод проверяет существует ли в коллекции пользователь с переданным логином
     *
     * @param login - логин пользовавтеля для поиска
     * @return true - если пользователь с предоставленным логином уже существует в коллекции,
     * false - если нет такого пользователя в коллекции
     */
    @Override
    public boolean checkUserWithSameLoginExist(String login) {
        boolean existUserWithSameLogin = userDAO.findUserByLogin(login) != null;
        log.info("now: " + LocalDateTime.now() + " checkUserWithSameLoginExist: " + existUserWithSameLogin);
        return existUserWithSameLogin;
    }


    @Override
    public UserDtoResponse getUserById(UUID uuid) {
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        Optional<User> optionalUser = userDAO.findUserById(uuid);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            userDtoResponse = userMapper.entityToResponseDto(user);
        }
        return userDtoResponse;
    }


    @Override
    public UserDtoRequest createUser(UserDtoRequest userDtoRequest) {
        if (isCorrectLoginEmail(userDtoRequest.getLogin(), userDtoRequest.getEmail())
                && isCorrectPasswords(userDtoRequest.getPassword(), userDtoRequest.getRepeatPassword())) {

            User newUser = userMapper.requestDtoToEntity(userDtoRequest);
            newUser.setUserStatus(UserStatus.STATUS_CREATED);
            userDAO.save(newUser);
            userDtoRequest.setId(newUser.getId());
        }
        return userDtoRequest;
    }

    @Override
    public void updateUser(UUID id, UserDtoRequest userDtoRequest) {
        if (userDAO.userWithIdIsExist(id) && Objects.equals(id, userDtoRequest.getId())) {
            User user = userMapper.requestDtoToEntity(userDtoRequest);
            user.setId(id);
            userDAO.update(user);
        }
    }

    @Override
    public ArrayList<UserDtoResponse> getAllUsers() {
        ArrayList<UserDtoResponse> userDtoResponseList = new ArrayList<>();
        for (User user : userDAO.getAllUsers()) {
            userDtoResponseList.add(getUserById(user.getId()));
        }
        return userDtoResponseList;
    }

    @Override
    public UserDtoResponse deleteUser(UUID id) {
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        Optional<User> optionalUser = Optional.ofNullable(userDAO.deleteByPK(id));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            userDtoResponse = userMapper.entityToResponseDto(user);
            userDtoResponse.getTasksIdList().forEach(taskDAO::deleteByPK);
        }
        return userDtoResponse;
    }

    public CustomValidator<User> getUserValidator() {
        return userValidator;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public UserDtoEntityMapper getUserMapper() {
        return userMapper;
    }

    /**
     * Метод проверяет что логин и email не пустые и пользователя с такими данными не существует
     *
     * @param login - логин пользователя
     * @param email - email пользователя
     * @return - true - логин и email не пусты и пользователя с такими данными не существует, false - в любом ином случае
     */
    private boolean isCorrectLoginEmail(String login, String email) {
        return (login != null) && (email != null) &&
                !(userDAO.emailExist(email) || userDAO.loginExist(login));
    }

    /**
     * Метод проверяет что введенные пароли не пусты и совпадают
     *
     * @param password       - пароль
     * @param repeatPassword - подтверждение пароля
     * @return - true - пароли не пусты и совпадают, false - в любом ином случае
     */
    private boolean isCorrectPasswords(String password, String repeatPassword) {
        return (password != null) && password.equals(repeatPassword);
    }
}
