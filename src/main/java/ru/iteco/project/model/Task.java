package ru.iteco.project.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Модель данных представляющая задание для исполнения
 */
public class Task implements Identified<UUID> {

    private static final long serialVersionUID = -7931737332645464539L;

    /*** Уникальный id задания */
    private UUID id;

    /*** Уникальный id заказчика */
    private User customer;

    /*** Уникальный id исполнителя */
    private User executor;

    /*** Имя задания */
    private String name;

    /*** Описание задания */
    private String description;

    /*** Время и дата размещения задания */
    private final LocalDateTime taskCreationDate = LocalDateTime.now();

    /*** Дата и время до наступления которой необходимо выполнить задание */
    private LocalDateTime taskCompletionDate;

    /*** Время и дата последнего обновления задания */
    private LocalDateTime lastTaskUpdateDate;

    /*** Стоимость исполнения задания */
    private BigDecimal price;

    /*** Статус задания */
    private TaskStatus taskStatus;

    /*** Решение задания */
    private String taskDecision;

    public Task() {
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public User getExecutor() {
        return executor;
    }

    public void setExecutor(User executor) {
        this.executor = executor;
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

    public LocalDateTime getTaskCreationDate() {
        return taskCreationDate;
    }

    public LocalDateTime getTaskCompletionDate() {
        return taskCompletionDate;
    }

    public void setTaskCompletionDate(LocalDateTime taskCompletionDate) {
        this.taskCompletionDate = taskCompletionDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskDecision() {
        return taskDecision;
    }

    public void setTaskDecision(String taskDecision) {
        this.taskDecision = taskDecision;
    }

    @Override
    public UUID getId() {
        return id;
    }

    public LocalDateTime getLastTaskUpdateDate() {
        return lastTaskUpdateDate;
    }

    public void setLastTaskUpdateDate(LocalDateTime lastTaskUpdateDate) {
        this.lastTaskUpdateDate = lastTaskUpdateDate;
    }

}
