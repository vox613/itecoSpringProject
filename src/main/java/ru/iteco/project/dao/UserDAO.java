package ru.iteco.project.dao;

import ru.iteco.project.model.Role;
import ru.iteco.project.model.User;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface UserDAO extends GenericDAO<User, UUID> {

    boolean loginExist(String login);

    boolean emailExist(String email);

    User findUserByLogin(String login);

    User findUserByFIO(String firstName, String secondName, String lastName);

    User findUserByLoginOrEmail(String login, String email);

    List<User> getAllUsersByRole(Role role);

    Collection<User> getAllUsers();

}
