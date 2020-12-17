package ru.iteco.project.service.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
@PropertySource(value = {"classpath:application.properties"})
public class ContractDtoEntityMapper implements DtoEntityMapper<Contract, ContractDtoRequest, ContractDtoResponse> {

    /*** Объект доступа к DAO слою Пользователей*/
    private final UserDAO userDAO;

    /*** Объект доступа к DAO слою Заданий*/
    private final TaskDAO taskDAO;

    /*** Установленный формат даты и времени*/
    @Value("${format.date.time}")
    private String formatDateTime;


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
            contractDtoResponse.setTimeOfContractConclusion(entity.getTimeOfContractConclusion().format(DateTimeFormatter.ofPattern(formatDateTime)));
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
            contract.setContractStatus(ContractStatus.REGISTERED);
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
    public void requestDtoToEntity(ContractDtoRequest requestDto, Contract contract, Role role) {
        if (requestDto != null) {
            if (Role.CUSTOMER.equals(role)) {
                contract.setContractStatus(ContractStatus.valueOf(requestDto.getContractStatus()));
            }
        }
    }

}
