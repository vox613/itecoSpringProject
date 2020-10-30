package ru.iteco.project.service.mappers;

import ru.iteco.project.controller.dto.DtoInterface;
import ru.iteco.project.model.Identified;

import java.util.UUID;

public interface DtoEntityMapper<E extends Identified<UUID>, R extends DtoInterface, S extends DtoInterface> {

    S entityToResponseDto(E entity);

    E requestDtoToEntity(R requestDto);

}
