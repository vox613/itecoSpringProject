package ru.iteco.project.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.iteco.project.model.Role;

import java.util.Objects;
import java.util.UUID;

public class UserDto implements DtoInterface {

    public UserDto() {
    }

    /*** Уникальный id пользователя */
    private UUID id;

    /*** Имя пользователя */
    private String firstName;

    /*** Фамилия пользователя */
    private String secondName;

    /*** Отчество пользователя */
    private String lastName;

    /*** Логин пользователя */
    private String login;

    /*** Пароль пользователя */
    private String password;

    /*** Подтверждение пароля */
    private String repeatPassword;

    /*** Email пользователя */
    private String email;

    /*** Номер телефона пользователя */
    private String phoneNumber;

    /*** Роль пользователя */
    private Role role;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @JsonIgnore
    public String getRepeatPassword() {
        return repeatPassword;
    }

    @JsonProperty
    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return firstName.equals(userDto.firstName) &&
                secondName.equals(userDto.secondName) &&
                Objects.equals(lastName, userDto.lastName) &&
                login.equals(userDto.login) &&
                password.equals(userDto.password) &&
                email.equals(userDto.email) &&
                phoneNumber.equals(userDto.phoneNumber) &&
                role == userDto.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, secondName, lastName, login, password, email, phoneNumber, role);
    }
}
