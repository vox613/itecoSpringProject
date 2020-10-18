package ru.iteco.project.service;

import ru.iteco.project.model.User;
import ru.iteco.project.model.UserStatus;

import java.util.ArrayList;
import java.util.List;

public interface UserService {

    void addUser(User user);

    User getUserByLogin(String login);

    ArrayList<User> getAllUsers();

    void addAllUsers(List<User> userList);

    User deleteUser(User user);

    void changeUserStatusTo(User user, UserStatus userStatus);

    boolean checkUserWithSameLoginExist(String login);

}
