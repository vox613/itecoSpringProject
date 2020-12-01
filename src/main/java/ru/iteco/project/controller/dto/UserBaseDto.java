package ru.iteco.project.controller.dto;

import ru.iteco.project.model.Role;

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
    private Role role;

    /*** Кошелек пользователя */
    private BigDecimal wallet = new BigDecimal(0);


    public UserBaseDto() {
    }

    public UserBaseDto(UUID id, String firstName, String secondName, String lastName, String login, String email,
                       String phoneNumber, Role role, BigDecimal wallet) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.login = login;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public BigDecimal getWallet() {
        return wallet;
    }

    public void setWallet(BigDecimal wallet) {
        this.wallet = wallet;
    }

}
