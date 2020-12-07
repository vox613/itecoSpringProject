package ru.iteco.project.service.mappers;

import ru.iteco.project.controller.dto.DtoInterface;
import ru.iteco.project.model.Identified;

import java.util.UUID;

/**
 * Общий интерфейс для мапперов сущностей в dto и наоборот
 *
 * @param <E>  - entity, модель данных, хранимая в БД
 * @param <RQ> - request, объект dto содержащий данные для создание entity
 * @param <RS> - response, объект dto содержащий данные для отображения информации на фронте
 */
public interface DtoEntityMapper<E extends Identified<UUID>, RQ extends DtoInterface, RS extends DtoInterface> {

    /**
     * Метод преобразует entity в response
     *
     * @param entity - модель данных, хранимая в БД
     * @return - объект dto содержащий данные для отображения информации на фронте
     */
    RS entityToResponseDto(E entity);

    /**
     * Метод преобразует request в entity
     *
     * @param requestDto - объект dto содержащий данные для создание entity
     * @return entity, модель данных, хранимая в БД
     */
    E requestDtoToEntity(RQ requestDto);

}
