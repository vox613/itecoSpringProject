package ru.iteco.project.service;

import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.model.User;
import ru.iteco.project.model.UserStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности User
 */
public interface UserService {

    /**
     * Метод сохранения пользователя в коллекцию
     *
     * @param user - пользователь для сохраннения
     */
    void addUser(User user);

    /**
     * Метод получения пользователя по логину
     *
     * @param login - логин пользователя
     * @return - объект пользователя, у которого логин соответствует переданному или null,
     * если такой пользователь не был найден
     */
    User getUserByLogin(String login);

    /**
     * Метод довавляет в коллекцию переданный список пользователей, перезаписывая информацию о тех,
     * которые уже присутствуют в коллекции
     *
     * @param userList - список пользователей для добавления в коллекцию
     */
    void addAllUsers(List<User> userList);

    /**
     * Метод удаляет пользователя из коллекции
     *
     * @param user - пользователь для удаления
     * @return - удаленный пользователь
     */
    User deleteUser(User user);

    /**
     * Метод изменения статуса пользователя на переданный в агументах
     *
     * @param user       - пользователь статус которого необходимо изменить
     * @param userStatus - статус на которой меняется состояние пользователя
     */
    void changeUserStatusTo(User user, UserStatus userStatus);

    /**
     * Метод проверяет существует ли в коллекции пользователь с переданным логином
     *
     * @param login - логин пользовавтеля для поиска
     * @return true - если пользователь с предоставленным логином уже существует в коллекции,
     * false - если нет такого пользователя в коллекции
     */
    boolean checkUserWithSameLoginExist(String login);

    UserDtoResponse getUserById(UUID id);

    UserDtoRequest createUser(UserDtoRequest userDtoRequest);

    void updateUser(UUID id, UserDtoRequest userDtoRequest);

    ArrayList<UserDtoResponse> getAllUsers();

    /**
     * Метод удаляет пользователя из коллекции
     *
     * @param id - id пользователя для удаления
     * @return - UserDto удаленного пользователя
     */
    UserDtoResponse deleteUser(UUID id);

}
