package ru.iteco.project.controller.dto;

import ru.iteco.project.model.UserStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс для формирования dto объекта сущности User, содержащего данные для отображения на фронте
 */
public class UserDtoResponse extends UserBaseDto {

    /*** Статус пользователя */
    private UserStatus userStatus;

    /*** Список id заданий пользователя */
    private final List<UUID> tasksIdList = new ArrayList<>();


    public UserDtoResponse() {
    }

    public List<UUID> getTasksIdList() {
        return tasksIdList;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
}
