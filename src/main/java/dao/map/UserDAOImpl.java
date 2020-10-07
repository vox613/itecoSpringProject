package dao.map;

import dao.UserDAO;
import model.Role;
import model.User;

import java.util.*;

public class UserDAOImpl extends AbstractDao<User, UUID> implements UserDAO {


    public UserDAOImpl() {
        super(User.class, new HashMap<>());
    }

    @Override
    public boolean loginExist(String login) {
        for (User user : elements.values()) {
            if ((user != null) && user.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean emailExist(String email) {
        for (User user : elements.values()) {
            if ((user != null) && user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public User findUserByLogin(String login) {
        for (User user : elements.values()) {
            if ((user != null) && user.getLogin().equals(login)) {
                return user;
            }
        }
        return null;
    }

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

    @Override
    public User findUserByLoginOrEmail(String login, String email) {
        for (User user : elements.values()) {
            if ((user != null) && (user.getLogin().equals(login) || user.getEmail().equals(email))) {
                return user;
            }
        }
        return null;
    }

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

    @Override
    public Collection<User> getAllUsers() {
        return getAll();
    }

}
