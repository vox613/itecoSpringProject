package ru.iteco.project.controller.dto;

/**
 * Класс хранящий данные для формирования запроса создания/обновления сущности Contract
 */
public class ContractDtoRequest extends ContractBaseDto {

    /*** Код подтверждения */
    private String confirmationCode;

    /*** Повтор кода подтверждения  */
    private String repeatConfirmationCode;

    /*** Изменение статуса контракта  */
    private String updateContractStatus;


    public ContractDtoRequest(String confirmationCode, String repeatConfirmationCode) {
        this.confirmationCode = confirmationCode;
        this.repeatConfirmationCode = repeatConfirmationCode;
    }

    public ContractDtoRequest() {
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public String getRepeatConfirmationCode() {
        return repeatConfirmationCode;
    }

    public void setRepeatConfirmationCode(String repeatConfirmationCode) {
        this.repeatConfirmationCode = repeatConfirmationCode;
    }

    public String getUpdateContractStatus() {
        return updateContractStatus;
    }

    public void setUpdateContractStatus(String updateContractStatus) {
        this.updateContractStatus = updateContractStatus;
    }
}
