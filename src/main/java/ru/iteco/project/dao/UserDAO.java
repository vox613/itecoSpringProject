package ru.iteco.project.dao;

import ru.iteco.project.model.User;
import ru.iteco.project.model.UserRole;
import ru.iteco.project.model.UserStatus;

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
    Optional<User> findUserByLogin(String login);

    /**
     * Метод осуществляет поиск всех пользователей по заданной роли
     *
     * @param role - роль пользователя
     * @return - список всех пользователей с указанной ролью
     */
    List<User> getAllUsersByRole(UserRole role);

    /**
     * Метод осуществляет поиск всех пользователей по заданному статусу
     *
     * @param userStatus - статус пользователя
     * @return - список всех пользователей с указанным статусом
     */
    List<User> getAllUsersByStatus(UserStatus userStatus);

    /**
     * Метод получения пользователя по id
     *
     * @param uuid - уникальный идентификатор пользователя
     * @return Optional с объектом User или с null, если такого пользователя не существует
     */
    Optional<User> findUserById(UUID uuid);

    /**
     * Метод проверяет существование пользователя с заданным id в системе
     *
     * @param uuid - уникальный идентификатор пользователя
     * @return - true - пользователь с заданным id существует,
     * false - пользователя с заданным id не существует
     */
    boolean userWithIdIsExist(UUID uuid);

    /**
     * Метод обновления статуса пользователя
     * @param user - сущность пользователя
     * @param userStatusEnum - элеммент перечисления статусов пользователя
     * @return - Optional с обновленным объектом User
     */
    Optional<User> updateUserStatus(User user, UserStatus.UserStatusEnum userStatusEnum);
}
