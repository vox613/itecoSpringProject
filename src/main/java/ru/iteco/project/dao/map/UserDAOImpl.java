package ru.iteco.project.dao.map;

import org.springframework.stereotype.Repository;
import ru.iteco.project.dao.UserDAO;
import ru.iteco.project.model.Role;
import ru.iteco.project.model.User;

import java.util.*;

/**
 * Класс реализующий функционал доступа к данным о пользователях
 */
@Repository
public class UserDAOImpl extends AbstractDao<User, UUID> implements UserDAO {


    public UserDAOImpl() {
        super(User.class, new HashMap<>());
    }

    /**
     * Метод проверяет используется ли переданный в аргументах логин
     *
     * @param login - логин, существование которого проверяется
     * @return - true - пользователь с данным логином присутстует в коллекции,
     * false - пользователь с данным логином отсутствует в коллекции
     */
    @Override
    public boolean loginExist(String login) {
        for (User user : elements.values()) {
            if ((user != null) && user.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Метод проверяет используется ли переданный в аргументах Email
     *
     * @param email - Email, существование которого проверяется
     * @return - true - пользователь с данным Email присутстует в коллекции,
     * false - пользователь с данным Email отсутствует в коллекции
     */
    @Override
    public boolean emailExist(String email) {
        for (User user : elements.values()) {
            if ((user != null) && user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Метод осуществляет поиск пользователя по логину
     *
     * @param login - логин пользователя
     * @return - объект пользователя, соответствующий данному логину,
     * или null, если пользователя с данным лолгином нет в коллекции
     */
    @Override
    public User findUserByLogin(String login) {
        for (User user : elements.values()) {
            if ((user != null) && user.getLogin().equals(login)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Метод осуществляет поиск пользователя по email
     *
     * @param email - email пользователя
     * @return - объект пользователя, соответствующий данному email,
     * или null, если пользователя с данным email нет в коллекции
     */
    @Override
    public User findUserByEmail(String email) {
        for (User user : elements.values()) {
            if ((user != null) && user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Метод осуществляет поиск пользователя по ФИО
     *
     * @param firstName  - имя пользователя
     * @param secondName - фамилия пользователя
     * @param lastName   - отчество пользователя
     * @return - объект пользователя, соответствующий данному ФИО,
     * или null, если пользователя с данным ФИО нет в коллекции
     */
    @Override
    public User findUserByFIO(String firstName, String secondName, String lastName) {
        for (User user : elements.values()) {
            if ((user != null) &&
                    user.getFirstName().equals(firstName) &&
                    user.getSecondName().equals(secondName) &&
                    user.getLastName().equals(lastName)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Метод осуществляет поиск пользователя по логину или Email
     *
     * @param login - логин пользователя
     * @param email - Email пользователя
     * @return - объект пользователя, соответствующий данному логину или Email,
     * или null, если пользователя с данным лолгином или Email нет в коллекции
     */
    @Override
    public User findUserByLoginOrEmail(String login, String email) {
        for (User user : elements.values()) {
            if ((user != null) && (user.getLogin().equals(login) || user.getEmail().equals(email))) {
                return user;
            }
        }
        return null;
    }

    /**
     * Метод осуществляет поиск всех пользователей по заданной роли
     *
     * @param role - роль пользователя
     * @return - список всех пользователей с указанной ролью
     */
    @Override
    public List<User> getAllUsersByRole(Role role) {
        ArrayList<User> users = new ArrayList<>();
        for (User user : elements.values()) {
            if ((user != null) && user.getRole().equals(role)) {
                users.add(user);
            }
        }
        return users;
    }

    /**
     * Метод получения списка всех пользователей из коллекции
     *
     * @return - список вссех пользователей в коллекции
     */
    @Override
    public Collection<User> getAllUsers() {
        return getAll();
    }

    @Override
    public Optional<User> findUserById(UUID uuid) {
        return Optional.ofNullable(getByPK(uuid));
    }

    @Override
    public boolean userWithIdIsExist(UUID uuid) {
        return Optional.ofNullable(getByPK(uuid)).isPresent();
    }


}
