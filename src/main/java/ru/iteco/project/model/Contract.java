package ru.iteco.project.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Модель данных представляющая договор между исполнителем и заказчиком
 */
public class Contract implements Identified<UUID> {

    private static final long serialVersionUID = -7931737332645464539L;
    /*** Уникальный id договора */
    private UUID id;

    /*** Испольнитель задания */
    private User customer;

    /*** Испольнитель задания */
    private User executor;

    /*** Дата и всремя заключения договора */
    private LocalDateTime timeOfContractConclusion = LocalDateTime.now();

    /*** Задание - предмет договора */
    private Task task;

    /*** Статус договора */
    private ContractStatus contractStatus;

    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getExecutor() {
        return executor;
    }

    public void setExecutor(User executor) {
        this.executor = executor;
    }

    public LocalDateTime getTimeOfContractConclusion() {
        return timeOfContractConclusion;
    }

    public void setTimeOfContractConclusion(LocalDateTime timeOfContractConclusion) {
        this.timeOfContractConclusion = timeOfContractConclusion;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public ContractStatus getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(ContractStatus contractStatus) {
        this.contractStatus = contractStatus;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }
}
