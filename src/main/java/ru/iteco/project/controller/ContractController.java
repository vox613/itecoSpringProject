package ru.iteco.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.iteco.project.controller.dto.ContractDtoRequest;
import ru.iteco.project.controller.dto.ContractDtoResponse;
import ru.iteco.project.service.ContractService;

import java.util.List;
import java.util.UUID;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с Contract
 */
@RestController
public class ContractController {

    /**
     * Объект сервисного слоя для Contract
     */
    private final ContractService contractService;

    @Autowired
    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }


    /**
     * Контроллер возвращает список всех созданных контрактов
     *
     * @return - список ContractDtoResponse
     */
    @GetMapping("/contracts")
    List<ContractDtoResponse> getAllContracts() {
        return contractService.getAllContracts();
    }


    /**
     * Контроллер возвращает ContractDtoResponse контракта с заданным id
     *
     * @param id - уникальный идентификатор контракта
     * @return ContractDtoResponse заданного контракта или пустой ContractDtoResponse, если данный контракт не существует
     */
    @GetMapping(value = "/contracts/{id}")
    public ContractDtoResponse getContract(@PathVariable UUID id) {
        return contractService.getContractById(id);
    }


    /**
     * Создает новый контракт для исполнителя {userId}
     *
     * @param contractDtoRequest - тело запроса на создание контракта
     * @return Тело запроса на создание контракта с уникальным проставленным id,
     * или тело запроса с id = null, если создать контракт не удалось
     */
    @PostMapping(value = "/contracts")
    public ContractDtoRequest createContract(@RequestBody ContractDtoRequest contractDtoRequest) {
        return contractService.createContract(contractDtoRequest);
    }


    /**
     * Обновляет существующий контракт {id} от имени заказчика {userId}
     *
     * @param id                 - уникальный идентификатор контракта
     * @param userId             - уникальный идентификатор пользователя инициировавшего процесс
     * @param contractDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/contracts/{id}")
    public void updateContract(@PathVariable UUID id,
                               @RequestParam UUID userId,
                               @RequestBody ContractDtoRequest contractDtoRequest) {

        contractService.updateContract(id, userId, contractDtoRequest);
    }


    /**
     * Удаляет контракт с заданным id
     *
     * @param id - уникальный идентификатор контракта для удаления
     * @return - объект ContractDtoResponse с данными удаленного контракта
     */
    @DeleteMapping(value = "/contracts/{id}")
    public ContractDtoResponse deleteContract(@PathVariable UUID id) {
        return contractService.deleteContract(id);
    }

}
