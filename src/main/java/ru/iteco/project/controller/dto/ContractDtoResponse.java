package ru.iteco.project.controller.dto;

import ru.iteco.project.model.ContractStatus;

/**
 * Класс для формирования dto объекта сущности Contract, содержащего данные для отображения на фронте
 */
public class ContractDtoResponse extends ContractBaseDto {

    /*** Дата и всремя заключения договора */
    private String timeOfContractConclusion;

    /*** Статус договора */
    private ContractStatus contractStatus;

    /*** Заказчик */
    private UserBaseDto customer;

    /*** Исполнитель */
    private UserBaseDto executor;

    /*** Задание */
    private TaskBaseDto task;


    public ContractDtoResponse() {
    }


    public String getTimeOfContractConclusion() {
        return timeOfContractConclusion;
    }

    public void setTimeOfContractConclusion(String timeOfContractConclusion) {
        this.timeOfContractConclusion = timeOfContractConclusion;
    }

    public ContractStatus getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(ContractStatus contractStatus) {
        this.contractStatus = contractStatus;
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

    public TaskBaseDto getTask() {
        return task;
    }

    public void setTask(TaskBaseDto task) {
        this.task = task;
    }
}
