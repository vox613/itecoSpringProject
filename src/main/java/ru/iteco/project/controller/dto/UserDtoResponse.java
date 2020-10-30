package ru.iteco.project.controller.dto;

import java.util.ArrayList;
import java.util.List;

public class UserDtoResponse extends UserBaseDto {

    private final List<TaskDtoResponse> tasksIdList = new ArrayList<>();

    public UserDtoResponse() {
    }


    public List<TaskDtoResponse> getTasksIdList() {
        return tasksIdList;
    }
}
