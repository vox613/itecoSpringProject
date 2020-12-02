package ru.iteco.project.controller.dto;

import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.UUID;

/**
 * Класс хранящий данные для формирования запроса создания/обновления сущности Task
 */
public class TaskDtoRequest extends TaskBaseDto {

    /*** Уникальный id пользователя инициировавшего действие */
    private UUID userId;

    /*** Решение задания*/
    private String taskDecision;

    /*** Статус задания*/
    private String taskStatus;

    /*** Список ошибок валидации запроса */
    private List<ObjectError> errors;


    public TaskDtoRequest() {
    }


    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getTaskDecision() {
        return taskDecision;
    }

    public void setTaskDecision(String taskDecision) {
        this.taskDecision = taskDecision;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}
