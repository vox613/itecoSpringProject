package ru.iteco.project.service.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.ContractDtoRequest;
import ru.iteco.project.controller.dto.ContractDtoResponse;
import ru.iteco.project.dao.ContractStatusDAO;
import ru.iteco.project.exception.InvalidContractStatusException;
import ru.iteco.project.model.Contract;

import java.time.format.DateTimeFormatter;
import java.util.UUID;


import static ru.iteco.project.model.ContractStatus.ContractStatusEnum.PAID;
import static ru.iteco.project.model.UserRole.UserRoleEnum.CUSTOMER;

/**
 * Класс маппер для сущности Contract
 */
@Service
@PropertySource(value = {"classpath:application.yml", "classpath:errors.properties"})
public class ContractDtoEntityMapper implements DtoEntityMapper<Contract, ContractDtoRequest, ContractDtoResponse> {

    /*** Установленный формат даты и времени*/
    @Value("${format.date.time}")
    private String formatDateTime;

    @Value("${errors.contract.status.invalid}")
    private String invalidContractStatusMessage;


    private final ContractStatusDAO contractStatusDAO;


    public ContractDtoEntityMapper(ContractStatusDAO contractStatusDAO) {
        this.contractStatusDAO = contractStatusDAO;
    }

    @Override
    public ContractDtoResponse entityToResponseDto(Contract entity) {
        final ContractDtoResponse contractDtoResponse = new ContractDtoResponse();
        if (entity != null) {
            contractDtoResponse.setId(entity.getId());
            contractDtoResponse.setExecutorId(entity.getExecutor().getId());
            contractDtoResponse.setTaskId(entity.getTask().getId());
            contractDtoResponse.setTimeOfContractConclusion(entity.getTimeOfContractConclusion().format(DateTimeFormatter.ofPattern(formatDateTime)));
            contractDtoResponse.setContractStatus(entity.getContractStatus().getValue());
        }
        return contractDtoResponse;
    }

    @Override
    public Contract requestDtoToEntity(ContractDtoRequest requestDto) {
        Contract contract = new Contract();
        if (requestDto != null) {
            contract.setId(UUID.randomUUID());
            contract.setContractStatus(contractStatusDAO.findContractStatusByValue(PAID.name())
                    .orElseThrow(() -> new InvalidContractStatusException(invalidContractStatusMessage)));
        }
        return contract;
    }

    /**
     * Маппер для обновления заказчиком статуса договора
     *
     * @param requestDto - объект запроса
     * @param contract   - сущность договора
     * @param role       - роль пользователя инициировавшего процесс
     */
    public void requestDtoToEntity(ContractDtoRequest requestDto, Contract contract, String role) {
        if (requestDto != null) {
            if (CUSTOMER.name().equals(role)) {
                contract.setContractStatus(contractStatusDAO.findContractStatusByValue(requestDto.getContractStatus())
                        .orElseThrow(() -> new InvalidContractStatusException(invalidContractStatusMessage)));
            }
        }
    }

}
