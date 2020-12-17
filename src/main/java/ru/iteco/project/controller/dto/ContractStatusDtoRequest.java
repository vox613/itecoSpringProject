package ru.iteco.project.controller.dto;

import java.util.UUID;

/**
 * Класс хранящий данные для формирования запроса создания/обновления сущности ContractStatus
 */
public class ContractStatusDtoRequest extends ContractStatusBaseDto {

    /*** Уникальный id пользователя совершающего действие*/
    private UUID userId;


    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
