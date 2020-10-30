package ru.iteco.project.controller.dto;

public class UserDtoRequest extends UserBaseDto {

    /*** Пароль пользователя */
    private String password;

    /*** Подтверждение пароля */
    private String repeatPassword;

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
}
