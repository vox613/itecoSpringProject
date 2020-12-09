package ru.iteco.project.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Модель данных представляющая договор между исполнителем и заказчиком
 */
@Entity
@Table(name = "contract")
public class Contract implements Identified<UUID> {

    private static final long serialVersionUID = -7931737332645464539L;

    /*** Уникальный id договора */
    @Id
    @Column
    private UUID id;

    /*** Заказчик */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    /*** Исполнитель задания */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, optional = false)
    @JoinColumn(name = "executor_id", nullable = false)
    private User executor;

    /*** Дата и время заключения договора */
    @Column(name = "time_contract_conclusion", nullable = false)
    private LocalDateTime timeOfContractConclusion = LocalDateTime.now();

    /*** Задание - предмет договора */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    /*** Статус договора */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "contract_status_id", nullable = false)
    private ContractStatus contractStatus;


    public Contract() {
    }

    public Contract(UUID id, User customer, User executor, LocalDateTime timeOfContractConclusion, Task task,
                    ContractStatus contractStatus) {
        this.id = id;
        this.customer = customer;
        this.executor = executor;
        this.timeOfContractConclusion = timeOfContractConclusion;
        this.task = task;
        this.contractStatus = contractStatus;
    }

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
