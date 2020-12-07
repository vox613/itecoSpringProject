package ru.iteco.project.controller.dto;

import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Класс хранящий данные для формирования запроса создания/обновления сущности User
 */
public class UserDtoRequest extends UserBaseDto {

    /*** Пароль пользователя */
    private String password;

    /*** Подтверждение пароля */
    private String repeatPassword;

    /*** Список ошибок валидации запроса */
    private List<ObjectError> errors;

    public UserDtoRequest() {
    }

    public UserDtoRequest(String password, String repeatPassword) {
        this.password = password;
        this.repeatPassword = repeatPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}
