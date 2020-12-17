package ru.iteco.project.controller.searching;


import java.math.BigDecimal;

/**
 * Класс - dto для поиска заданий с переданными ограничениями по полям
 */
public class TaskSearchDto implements SearchDto<TaskSearchDto> {

    /*** Статус задания */
    private String taskStatus;

    /*** Имя задания */
    private String name;

    /*** Описание задания */
    private String description;

    /*** Дата и время до наступления которой необходимо выполнить задание */
    private String taskCompletionDate;

    /*** Стоимость исполнения задания */
    private BigDecimal price;

    /*** Дата и время создания пользователя */
    private String createdAt;

    /*** Дата и время последнего обновления пользователя */
    private String updatedAt;

    /*** Минимальная стоимость задания */
    private String minPrice;

    /*** Максимальная стоимость задания */
    private String maxPrice;


    public TaskSearchDto() {
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    @Override
    public TaskSearchDto searchData() {
        return this;
    }
}
