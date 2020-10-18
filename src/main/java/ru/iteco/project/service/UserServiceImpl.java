package ru.iteco.project.service;

import ru.iteco.project.dao.UserDAO;
import ru.iteco.project.model.User;
import ru.iteco.project.model.UserStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    private static final Logger log = LogManager.getLogger(UserServiceImpl.class.getName());

    private UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        log.info("UserServiceImpl");
        this.userDAO = userDAO;
    }


    @Override
    public void addUser(User user) {
        log.info("now: " + LocalDateTime.now() + " addUser: " + user);
        userDAO.save(user);
    }


    @Override
    public User getUserByLogin(String login) {
        User userByLogin = userDAO.findUserByLogin(login);
        log.info("now: " + LocalDateTime.now() + " ByLogin: " + login + " get User: " + userByLogin);
        return userByLogin;
    }

    @Override
    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(userDAO.getAllUsers());
    }

    @Override
    public void addAllUsers(List<User> userList) {
        log.info("now: " + LocalDateTime.now() + " addAllUsers: " + userList.toString());
        userDAO.addAll(userList);
    }


    @Override
    public User deleteUser(User user) {
        log.info("now: " + LocalDateTime.now() + " deleteUser: " + user);
        return userDAO.delete(user);
    }

    @Override
    public void changeUserStatusTo(User user, UserStatus userStatus) {
        user.setUserStatus(userStatus);
        userDAO.update(user);
        log.info("now: " + LocalDateTime.now() + " changeUser: " + user + "StatusTo: " + userStatus);
    }

    @Override
    public boolean checkUserWithSameLoginExist(String login) {
        boolean existUserWithSameLogin = userDAO.findUserByLogin(login) != null;
        log.info("now: " + LocalDateTime.now() + " checkUserWithSameLoginExist: " + existUserWithSameLogin);
        return existUserWithSameLogin;
    }

}
