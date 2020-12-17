package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.controller.dto.ContractStatusDtoRequest;
import ru.iteco.project.controller.dto.ContractStatusDtoResponse;
import ru.iteco.project.controller.dto.TaskStatusDtoRequest;
import ru.iteco.project.controller.dto.TaskStatusDtoResponse;
import ru.iteco.project.dao.ContractDAO;
import ru.iteco.project.dao.ContractStatusDAO;
import ru.iteco.project.dao.TaskStatusDAO;
import ru.iteco.project.dao.UserDAO;
import ru.iteco.project.model.*;
import ru.iteco.project.service.mappers.ContractStatusDtoEntityMapper;
import ru.iteco.project.service.mappers.TaskStatusDtoEntityMapper;

import java.util.*;

import static ru.iteco.project.model.UserRole.UserRoleEnum.ADMIN;
import static ru.iteco.project.model.UserRole.UserRoleEnum.isEqualsUserRole;

/**
 * Класс реализует функционал сервисного слоя для работы со статусами контрактов
 */
@Service
public class ContractStatusServiceImpl implements ContractStatusService {

    private static final Logger log = LogManager.getLogger(ContractStatusServiceImpl.class.getName());


    private final ContractStatusDAO contractStatusDAO;
    private final ContractDAO contractsDAO;
    private final UserDAO userDAO;
    private final ContractService contractService;
    private final ContractStatusDtoEntityMapper contractStatusDtoEntityMapper;


    public ContractStatusServiceImpl(ContractStatusDAO contractStatusDAO, ContractDAO contractsDAO, UserDAO userDAO,
                                     ContractService contractService, ContractStatusDtoEntityMapper contractStatusDtoEntityMapper) {

        this.contractStatusDAO = contractStatusDAO;
        this.contractsDAO = contractsDAO;
        this.userDAO = userDAO;
        this.contractService = contractService;
        this.contractStatusDtoEntityMapper = contractStatusDtoEntityMapper;
    }


    /**
     *  По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     *  REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(readOnly = true)
    public ContractStatusDtoResponse getContractStatusById(UUID id) {
        ContractStatusDtoResponse contractStatusDtoResponse = new ContractStatusDtoResponse();
        Optional<ContractStatus> optionalContractStatusById = contractStatusDAO.findContractStatusById(id);
        if (optionalContractStatusById.isPresent()) {
            ContractStatus contractStatus = optionalContractStatusById.get();
            contractStatusDtoResponse = contractStatusDtoEntityMapper.entityToResponseDto(contractStatus);
        }
        return contractStatusDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ContractStatusDtoResponse createContractStatus(ContractStatusDtoRequest contractStatusDtoRequest) {
        ContractStatusDtoResponse  contractStatusDtoResponse = new ContractStatusDtoResponse();
        if (operationIsAllow(contractStatusDtoRequest)) {
            ContractStatus newContractStatus = contractStatusDtoEntityMapper.requestDtoToEntity(contractStatusDtoRequest);
            contractStatusDAO.save(newContractStatus);
            contractStatusDtoResponse = contractStatusDtoEntityMapper.entityToResponseDto(newContractStatus);
        }
        return contractStatusDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ContractStatusDtoResponse updateContractStatus(UUID id, ContractStatusDtoRequest contractStatusDtoRequest) {
        ContractStatusDtoResponse contractStatusDtoResponse = new ContractStatusDtoResponse();
        if (operationIsAllow(contractStatusDtoRequest) &&
                Objects.equals(id, contractStatusDtoRequest.getId()) &&
                contractStatusDAO.contractStatusWithIdIsExist(contractStatusDtoRequest.getId())) {

            ContractStatus contractStatus = contractStatusDtoEntityMapper.requestDtoToEntity(contractStatusDtoRequest);
            contractStatus.setId(id);
            contractStatusDAO.update(contractStatus);
            contractStatusDtoResponse = contractStatusDtoEntityMapper.entityToResponseDto(contractStatus);
        }
        return contractStatusDtoResponse;
    }

    /**
     *  По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public ArrayList<ContractStatusDtoResponse> getAllContractsStatuses() {
        ArrayList<ContractStatusDtoResponse> contractStatusDtoResponsesList = new ArrayList<>();
        for (ContractStatus contractStatus : contractStatusDAO.getAll()) {
            contractStatusDtoResponsesList.add(contractStatusDtoEntityMapper.entityToResponseDto(contractStatus));
        }
        return contractStatusDtoResponsesList;
    }

    /**
     *  SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     *  REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     *  должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteContractStatus(UUID id) {
        Optional<ContractStatus> contractStatusById = contractStatusDAO.findContractStatusById(id);
        if (contractStatusById.isPresent()) {
            Collection<Contract> allContractsByStatus = contractsDAO.findAllContractsByStatus(contractStatusById.get());
            allContractsByStatus.forEach(contract -> contractService.deleteContract(contract.getId()));
            contractStatusDAO.deleteByPK(id);
            return true;
        }
        return false;
    }

    /**
     * Метод проверяет разрешена ли для пользователя данная операция
     *
     * @param contractStatusDtoRequest - запрос
     * @return true - операция разрешена, false - операция запрещена
     */
    private boolean operationIsAllow(ContractStatusDtoRequest contractStatusDtoRequest) {
        if ((contractStatusDtoRequest != null) && (contractStatusDtoRequest.getUserId() != null)) {
            Optional<User> userById = userDAO.findUserById(contractStatusDtoRequest.getUserId());
            if (userById.isPresent()) {
                return isEqualsUserRole(ADMIN, userById.get());
            }
        }
        return false;
    }
}
