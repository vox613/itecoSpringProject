package ru.iteco.project.controller.dto;

import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.UUID;

/**
 * Базовый класс для общих полей dto сущности UserStatus
 */
public class UserStatusBaseDto implements DtoInterface {

    /*** Уникальный id статуса пользователя */
    private UUID id;

    /*** Наименование статуса пользователя */
    private String value;

    /*** Описание статуса пользователя */
    private String description;

    /*** Список ошибок валидации запроса */
    private List<ObjectError> errors;


    public UserStatusBaseDto() {
    }

    public UserStatusBaseDto(UUID id, String value) {
        this.id = id;
        this.value = value;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}
