package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.UserDto;
import ru.iteco.project.dao.UserDAO;
import ru.iteco.project.model.User;
import ru.iteco.project.model.UserStatus;
import ru.iteco.project.service.mappers.EntityDtoMapper;
import ru.iteco.project.service.validators.CustomValidator;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс реализует функционал сервисного слоя для работы с пользователями
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LogManager.getLogger(UserServiceImpl.class.getName());

    private UserDAO userDAO;
    private CustomValidator userValidator;
    private EntityDtoMapper<User, UserDto> userDtoMapper;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, CustomValidator userValidator, EntityDtoMapper<User, UserDto> userDtoMapper) {
        this.userDAO = userDAO;
        this.userValidator = userValidator;
        this.userDtoMapper = userDtoMapper;
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
     * Метод получает всех пользователей из коллекции
     *
     * @return - список всех пользователей из коллекции
     */
    @Override
    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(userDAO.getAllUsers());
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
    public UserDto getUserById(UUID uuid) {
        Optional<User> optionalUser = userDAO.findUserById(uuid);
        return userDtoMapper.entityToDto(optionalUser.orElseGet(User::new));
    }


    @Override
    public UserDto createUser(UserDto userDto) {
        if (!userDAO.emailExist(userDto.getEmail()) && !userDAO.loginExist(userDto.getLogin())) {
            User newUser = userDtoMapper.dtoToEntity(userDto);
            User savedUser = userDAO.save(newUser);
            userDto.setId(savedUser.getId());
        }
        return userDto;
    }

    @Override
    public UserDto updateUser(UUID id, UserDto userDto) {
        if (userDAO.userWithIdIsExist(id) && Objects.equals(id, userDto.getId())) {
            User user = userDtoMapper.dtoToEntity(userDto);
            user.setId(id);
            userDAO.update(user);
        }
        return userDto;
    }

    @Override
    public ArrayList<UserDto> getAllDtoUsers() {
        ArrayList<UserDto> userDtos = new ArrayList<>();
        for (User user : getAllUsers()) {
            userDtos.add(userDtoMapper.entityToDto(user));
        }
        return userDtos;
    }

    @Override
    public UserDto deleteUser(UUID id) {
        User user = userDAO.deleteByPK(id);
        return userDtoMapper.entityToDto(user);
    }

    public CustomValidator<User> getUserValidator() {
        return userValidator;
    }
}
