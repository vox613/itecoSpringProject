package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.dao.UserDAO;
import ru.iteco.project.model.Task;
import ru.iteco.project.model.User;
import ru.iteco.project.model.UserStatus;
import ru.iteco.project.service.mappers.UserDtoEntityMapper;
import ru.iteco.project.service.validators.CustomValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Класс реализует функционал сервисного слоя для работы с пользователями
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LogManager.getLogger(UserServiceImpl.class.getName());

    private final UserDAO userDAO;
    private final TaskServiceImpl taskService;
    private final CustomValidator userValidator;
    private final UserDtoEntityMapper userMapper;


    public UserServiceImpl(UserDAO userDAO, TaskServiceImpl taskService, CustomValidator userValidator,
                           UserDtoEntityMapper userMapper) {
        this.userDAO = userDAO;
        this.taskService = taskService;
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
        if (isCorrectLoginEmail(userDtoRequest.getLogin(), userDtoRequest.getEmail())) {
            User newUser = userMapper.requestDtoToEntity(userDtoRequest);
            newUser.setUserStatus(UserStatus.STATUS_CREATED);
            userDtoRequest.setUserStatus(UserStatus.STATUS_CREATED.name());
            userDAO.save(newUser);
            userDtoRequest.setId(newUser.getId());
        }
        return userDtoRequest;
    }

    @Override
    public List<UserDtoRequest> createBundleUsers(List<UserDtoRequest> userDtoRequestList) {
        return userDtoRequestList.stream().map(this::createUser).collect(Collectors.toList());
    }

    @Override
    public UserDtoResponse updateUser(UserDtoRequest userDtoRequest) {
        UserDtoResponse userDtoResponse = null;
        if (userDAO.userWithIdIsExist(userDtoRequest.getId())) {
            User user = userMapper.requestDtoToEntity(userDtoRequest);
            user.setId(userDtoRequest.getId());
            userDAO.update(user);
            userDtoResponse = userMapper.entityToResponseDto(user);
        }
        return userDtoResponse;
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
    public Boolean deleteUser(UUID id) {
        Optional<User> optionalUser = userDAO.findUserById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Task> allTasksByCustomer = taskService.getTaskDAO().findAllTasksByCustomer(user);
            allTasksByCustomer.forEach(task -> taskService.deleteTask(task.getId()));
            userDAO.deleteByPK(id);
            return true;
        }
        return false;
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
     * Метод проверяет что пользователя с таким логином и email не существует
     *
     * @param login - логин пользователя
     * @param email - email пользователя
     * @return - true - логин и email не пусты и пользователя с такими данными не существует, false - в любом ином случае
     */
    private boolean isCorrectLoginEmail(String login, String email) {
        return !(userDAO.emailExist(email) || userDAO.loginExist(login));
    }
}
