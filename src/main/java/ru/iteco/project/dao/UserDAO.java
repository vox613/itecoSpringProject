package ru.iteco.project.dao;

import ru.iteco.project.model.Role;
import ru.iteco.project.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал DAO слоя для сущности User
 */
public interface UserDAO extends GenericDAO<User, UUID> {

    /**
     * Метод проверяет используется ли переданный в аргументах логин
     *
     * @param login - логин, существование которого проверяется
     * @return - true - пользователь с данным логином присутстует в коллекции,
     * false - пользователь с данным логином отсутствует в коллекции
     */
    boolean loginExist(String login);

    /**
     * Метод проверяет используется ли переданный в аргументах Email
     *
     * @param email - Email, существование которого проверяется
     * @return - true - пользователь с данным Email присутстует в коллекции,
     * false - пользователь с данным Email отсутствует в коллекции
     */
    boolean emailExist(String email);

    /**
     * Метод осуществляет поиск пользователя по логину
     *
     * @param login - логин пользователя
     * @return - объект пользователя, соответствующий данному логину,
     * или null, если пользователя с данным лолгином нет в коллекции
     */
    User findUserByLogin(String login);

    /**
     * Метод осуществляет поиск пользователя по email
     *
     * @param email - email пользователя
     * @return - объект пользователя, соответствующий данному email,
     * или null, если пользователя с данным email нет в коллекции
     */
    User findUserByEmail(String email);

    /**
     * Метод осуществляет поиск пользователя по ФИО
     *
     * @param firstName  - имя пользователя
     * @param secondName - фамилия пользователя
     * @param lastName   - отчество пользователя
     * @return - объект пользователя, соответствующий данному ФИО,
     * или null, если пользователя с данным ФИО нет в коллекции
     */
    User findUserByFIO(String firstName, String secondName, String lastName);

    /**
     * Метод осуществляет поиск пользователя по логину или Email
     *
     * @param login - логин пользователя
     * @param email - Email пользователя
     * @return - объект пользователя, соответствующий данному логину или Email,
     * или null, если пользователя с данным лолгином или Email нет в коллекции
     */
    User findUserByLoginOrEmail(String login, String email);

    /**
     * Метод осуществляет поиск всех пользователей по заданной роли
     *
     * @param role - роль пользователя
     * @return - список всех пользователей с указанной ролью
     */
    List<User> getAllUsersByRole(Role role);

    /**
     * Метод получения списка всех пользователей из коллекции
     *
     * @return - список вссех пользователей в коллекции
     */
    Collection<User> getAllUsers();


    Optional<User> findUserById(UUID uuid);

    boolean userWithIdIsExist(UUID uuid);

}
