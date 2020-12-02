package ru.iteco.project.controller.dto;

/**
 * Класс для формирования dto объекта сущности Task, содержащего данные для отображения на фронте
 */
public class TaskDtoResponse extends TaskBaseDto {

    /*** Время и дата размещения задания */
    private String taskCreationDate;

    /*** Время и дата последнего обновления задания */
    private String lastTaskUpdateDate;

    /*** Статус задания */
    private String taskStatus;

    /*** Решение задания */
    private String taskDecision;

    /*** Заказчик */
    private UserBaseDto customer;

    /*** Исполнитель */
    private UserBaseDto executor;


    public TaskDtoResponse() {
    }

    public UserBaseDto getCustomer() {
        return customer;
    }

    public void setCustomer(UserBaseDto customer) {
        this.customer = customer;
    }

    public UserBaseDto getExecutor() {
        return executor;
    }

    public void setExecutor(UserBaseDto executor) {
        this.executor = executor;
    }

    public String getTaskCreationDate() {
        return taskCreationDate;
    }

    public void setTaskCreationDate(String taskCreationDate) {
        this.taskCreationDate = taskCreationDate;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskDecision() {
        return taskDecision;
    }

    public void setTaskDecision(String taskDecision) {
        this.taskDecision = taskDecision;
    }

    public String getLastTaskUpdateDate() {
        return lastTaskUpdateDate;
    }

    public void setLastTaskUpdateDate(String lastTaskUpdateDate) {
        this.lastTaskUpdateDate = lastTaskUpdateDate;
    }
}
