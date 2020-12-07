package ru.iteco.project.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Базовый класс для общих полей dto сущности User
 */
public class UserBaseDto implements DtoInterface {

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

    /*** Email пользователя */
    private String email;

    /*** Номер телефона пользователя */
    private String phoneNumber;

    /*** Роль пользователя */
    private String role;

    /*** Статус пользователя */
    private String userStatus;

    /*** Кошелек пользователя */
    private BigDecimal wallet = new BigDecimal(0);


    public UserBaseDto() {
    }

    public UserBaseDto(UUID id, String firstName, String secondName, String lastName, String login,
                       String email, String phoneNumber, String role, String userStatus, BigDecimal wallet) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.login = login;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.userStatus = userStatus;
        this.wallet = wallet;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public BigDecimal getWallet() {
        return wallet;
    }

    public void setWallet(BigDecimal wallet) {
        this.wallet = wallet;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
