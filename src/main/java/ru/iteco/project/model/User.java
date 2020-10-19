package ru.iteco.project.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Модель данных представляющая пользователей
 */
public class User implements Identified<UUID> {

    private static final long serialVersionUID = -7931737332645464539L;

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

    /*** Email пользователя */
    private String email;

    /*** Номер телефона пользователя */
    private String phoneNumber;

    /*** Роль пользователя */
    private Role role;

    /*** Статус пользователя */
    private UserStatus userStatus;

    /*** Кошелек пользователя */
    private BigDecimal wallet;


    @Override
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

    public String getPassword() {
        return password;
    }

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

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public BigDecimal getWallet() {
        return wallet;
    }

    public void setWallet(BigDecimal wallet) {
        this.wallet = wallet;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role=" + role +
                ", userStatus=" + userStatus +
                ", wallet=" + wallet +
                '}';
    }
}
