package ru.iteco.project.service.mappers;

import ru.iteco.project.controller.dto.DtoInterface;
import ru.iteco.project.model.Identified;

import java.util.UUID;

public interface EntityDtoMapper<T extends Identified<UUID>, P extends DtoInterface> {

    P entityToDto(T entityObject);

    T dtoToEntity(P dtoObject);

}
