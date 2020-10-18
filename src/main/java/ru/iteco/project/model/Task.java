package ru.iteco.project.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Task implements Identified<UUID> {

    private static final long serialVersionUID = -7931737332645464539L;

    private UUID id;
    private User customer;
    private String name;
    private String description;
    private LocalDateTime taskCreationDate = LocalDateTime.now();
    private LocalDateTime taskCompletionDate;
    private BigDecimal price;
    private TaskStatus taskStatus;


    @Override
    public UUID getId() {
        return id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getTaskCreationDate() {
        return taskCreationDate;
    }

    public void setTaskCreationDate(LocalDateTime taskCreationDate) {
        this.taskCreationDate = taskCreationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getTaskCompletionDate() {
        return taskCompletionDate;
    }

    public void setTaskCompletionDate(LocalDateTime taskCompletionDate) {
        this.taskCompletionDate = taskCompletionDate;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", customer=" + customer +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskCreationDate=" + taskCreationDate +
                ", taskCompletionDate=" + taskCompletionDate +
                ", price=" + price +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
