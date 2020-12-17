package ru.iteco.project.service.mappers;

import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.ContractStatusDtoRequest;
import ru.iteco.project.controller.dto.ContractStatusDtoResponse;
import ru.iteco.project.controller.dto.TaskStatusDtoRequest;
import ru.iteco.project.controller.dto.TaskStatusDtoResponse;
import ru.iteco.project.model.ContractStatus;
import ru.iteco.project.model.TaskStatus;

import java.util.UUID;

/**
 * Класс маппер для сущности ContractStatus
 */
@Service
public class ContractStatusDtoEntityMapper implements DtoEntityMapper<ContractStatus, ContractStatusDtoRequest, ContractStatusDtoResponse> {

    @Override
    public ContractStatusDtoResponse entityToResponseDto(ContractStatus entity) {
        ContractStatusDtoResponse contractStatusDtoResponse = new ContractStatusDtoResponse();
        if (entity != null) {
            contractStatusDtoResponse.setId(entity.getId());
            contractStatusDtoResponse.setValue(entity.getValue());
            contractStatusDtoResponse.setDescription(entity.getDescription());
        }
        return contractStatusDtoResponse;
    }

    @Override
    public ContractStatus requestDtoToEntity(ContractStatusDtoRequest requestDto) {
        ContractStatus contractStatus = new ContractStatus();
        if (requestDto != null) {
            contractStatus.setId(UUID.randomUUID());
            contractStatus.setValue(requestDto.getValue());
            contractStatus.setDescription(requestDto.getDescription());
        }
        return contractStatus;
    }
}
