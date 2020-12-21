package ru.iteco.project.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Базовый класс для общих полей dto сущности Task
 */
public class TaskBaseDto implements DtoInterface {

    /*** Уникальный id задания */
    private UUID id;

    /*** Уникальный id заказчика */
    private UUID customerId;

    /*** Уникальный id исполнителя */
    private UUID executorId;

    /*** Имя задания */
    private String name;

    /*** Описание задания */
    private String description;

    /*** Дата и время до наступления которой необходимо выполнить задание */
    private String taskCompletionDate;

    /*** Стоимость исполнения задания */
    private BigDecimal price;


    public TaskBaseDto() {
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskCompletionDate() {
        return taskCompletionDate;
    }

    public void setTaskCompletionDate(String taskCompletionDate) {
        this.taskCompletionDate = taskCompletionDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public UUID getExecutorId() {
        return executorId;
    }

    public void setExecutorId(UUID executorId) {
        this.executorId = executorId;
    }
}
