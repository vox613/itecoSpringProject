package ru.iteco.project.controller.dto;

import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.UUID;

/**
 * Класс хранящий данные для формирования запроса создания/обновления сущности Contract
 */
public class ContractDtoRequest extends ContractBaseDto {

    /*** Уникальный id пользователя инициировавшего действие */
    private UUID userId;

    /*** Код подтверждения */
    private String confirmationCode;

    /*** Повтор кода подтверждения  */
    private String repeatConfirmationCode;

    /*** Изменение статуса контракта  */
    private String contractStatus;

    /*** Список ошибок валидации запроса */
    private List<ObjectError> errors;


    public ContractDtoRequest(String confirmationCode, String repeatConfirmationCode) {
        this.confirmationCode = confirmationCode;
        this.repeatConfirmationCode = repeatConfirmationCode;
    }

    public ContractDtoRequest() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}
