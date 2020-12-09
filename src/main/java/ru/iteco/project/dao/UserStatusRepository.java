package ru.iteco.project.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iteco.project.domain.UserStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности UserStatus
 */
public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

    /**
     * Метод получения сущности статуса пользователя по его строковому представлению
     *
     * @param value - строковое представление статуса пользователя
     * @return - Объект Optional с сущностью статуса пользователя или с null, если статус пользователя с данным значением не существует
     */
    Optional<UserStatus> findUserStatusByValue(String value);

}
