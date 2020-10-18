package ru.iteco.project.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Contract implements Identified<UUID> {

    private static final long serialVersionUID = -7931737332645464539L;

    private UUID id;
    private User executor;
    private LocalDateTime timeOfContractConclusion = LocalDateTime.now();
    private Task task;
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

    @Override
    public String toString() {
        return "Contract{" +
                "id=" + id +
                ", executor=" + executor +
                ", timeOfContractConclusion=" + timeOfContractConclusion +
                ", task=" + task +
                ", contractStatus=" + contractStatus +
                '}';
    }
}
