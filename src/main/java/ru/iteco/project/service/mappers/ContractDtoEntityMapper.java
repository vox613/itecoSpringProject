package ru.iteco.project.service.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.ContractDtoRequest;
import ru.iteco.project.controller.dto.ContractDtoResponse;
import ru.iteco.project.dao.TaskDAO;
import ru.iteco.project.dao.UserDAO;
import ru.iteco.project.model.Contract;
import ru.iteco.project.model.ContractStatus;
import ru.iteco.project.model.Role;
import ru.iteco.project.model.Task;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

/**
 * Класс маппер для сущности Contract
 */
@Service
public class ContractDtoEntityMapper implements DtoEntityMapper<Contract, ContractDtoRequest, ContractDtoResponse> {

    /**
     * Объект доступа к DAO слою Пользователей
     */
    private final UserDAO userDAO;

    /**
     * Объект доступа к DAO слою Заданий
     */
    private final TaskDAO taskDAO;

    @Autowired
    public ContractDtoEntityMapper(UserDAO userDAO, TaskDAO taskDAO) {
        this.userDAO = userDAO;
        this.taskDAO = taskDAO;
    }


    @Override
    public ContractDtoResponse entityToResponseDto(Contract entity) {
        final ContractDtoResponse contractDtoResponse = new ContractDtoResponse();
        if (entity != null) {
            contractDtoResponse.setId(entity.getId());
            contractDtoResponse.setExecutorId(entity.getExecutor().getId());
            contractDtoResponse.setTaskId(entity.getTask().getId());
            contractDtoResponse.setTimeOfContractConclusion(entity.getTimeOfContractConclusion().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            contractDtoResponse.setContractStatus(entity.getContractStatus());
        }
        return contractDtoResponse;
    }

    @Override
    public Contract requestDtoToEntity(ContractDtoRequest requestDto) {
        Contract contract = new Contract();
        if (requestDto != null) {
            contract.setId(UUID.randomUUID());
            userDAO.findUserById(requestDto.getExecutorId()).ifPresent(contract::setExecutor);
            Optional<Task> taskById = taskDAO.findTaskById(requestDto.getTaskId());
            if (taskById.isPresent()) {
                Task task = taskById.get();
                contract.setCustomer(task.getCustomer());
                contract.setTask(task);
            }
            contract.setContractStatus(ContractStatus.CONTRACT_REGISTERED);
        }
        return contract;
    }

    public void requestDtoToEntity(ContractDtoRequest requestDto, Contract contract, Role role) {
        if (requestDto != null) {
            if (Role.ROLE_CUSTOMER.equals(role)) {
                contract.setContractStatus(ContractStatus.valueOf(requestDto.getUpdateContractStatus()));
            }
        }
    }

}