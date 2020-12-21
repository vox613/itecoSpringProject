package ru.iteco.project.controller.dto;

import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.UUID;

/**
 * Базовый класс для общих полей dto сущности UserRole
 */
public class UserRoleBaseDto implements DtoInterface {

    /*** Уникальный id роли пользователя */
    private UUID id;

    /*** Наименование роли пользователя */
    private String value;

    /*** Список ошибок валидации запроса */
    private List<ObjectError> errors;


    public UserRoleBaseDto() {
    }

    public UserRoleBaseDto(UUID id, String value) {
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

    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}
