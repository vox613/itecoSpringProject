package ru.iteco.project.controller.dto;

import java.util.UUID;

/**
 * Базовый класс для общих полей dto сущности Contract
 */
public class ContractBaseDto implements DtoInterface {

    /*** Уникальный id договора */
    private UUID id;

    /*** Уникальный id исполнителя */
    private UUID executorId;

    /*** Уникальное id задания */
    private UUID taskId;


    public ContractBaseDto() {
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getExecutorId() {
        return executorId;
    }

    public void setExecutorId(UUID executorId) {
        this.executorId = executorId;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }
}