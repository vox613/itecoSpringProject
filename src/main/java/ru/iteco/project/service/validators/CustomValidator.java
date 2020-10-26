package ru.iteco.project.service.validators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.iteco.project.model.Identified;

import java.util.UUID;

/**
 * Интерфейс описывает общий функционал валидации полей сущностей
 *
 * @param <T> тип объекта
 */
public interface CustomValidator<T extends Identified<UUID>> {
    Logger log = LogManager.getLogger(CustomValidator.class.getName());

    /**
     * Метод валидации полей конкретной сущности
     *
     * @param t - некоторая сущность
     */
    void validate(T t);

}
