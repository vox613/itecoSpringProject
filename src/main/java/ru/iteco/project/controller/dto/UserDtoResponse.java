package ru.iteco.project.controller.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс для формирования dto объекта сущности User, содержащего данные для отображения на фронте
 */
public class UserDtoResponse extends UserBaseDto {

    /*** Список id заданий пользователя */
    private final List<UUID> tasksIdList = new ArrayList<>();


    public UserDtoResponse() {
    }

    public List<UUID> getTasksIdList() {
        return tasksIdList;
    }

}
