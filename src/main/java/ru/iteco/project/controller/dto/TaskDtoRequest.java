package ru.iteco.project.controller.dto;

/**
 * Класс хранящий данные для формирования запроса создания/обновления сущности Task
 */
public class TaskDtoRequest extends TaskBaseDto {

    /*** Решение задания*/
    private String taskDecision;

    /*** Статус задания*/
    private String taskStatus;


    public TaskDtoRequest() {
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
}
