package ru.iteco.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteco.project.controller.dto.ContractBaseDto;
import ru.iteco.project.controller.dto.ContractDtoRequest;
import ru.iteco.project.controller.dto.ContractDtoResponse;
import ru.iteco.project.service.ContractService;
import ru.iteco.project.validator.ContractDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с Contract
 */
@RestController
@RequestMapping(value = "/api/v1/contracts")
public class ContractController {

    /*** Объект сервисного слоя для Contract*/
    private final ContractService contractService;

    /*** Объект валидатора для ContractDtoRequest*/
    private final ContractDtoRequestValidator contractDtoRequestValidator;


    public ContractController(ContractService contractService, ContractDtoRequestValidator contractDtoRequestValidator) {
        this.contractService = contractService;
        this.contractDtoRequestValidator = contractDtoRequestValidator;
    }


    /**
     * Контроллер возвращает список всех созданных контрактов
     *
     * @return - список ContractDtoResponse
     */
    @GetMapping
    ResponseEntity<List<ContractDtoResponse>> getAllContracts(@RequestParam(required = false) UUID userId) {
        List<ContractDtoResponse> allContracts = contractService.getAllContracts();
        return ResponseEntity.ok().body(allContracts);
    }


    /**
     * Контроллер возвращает ContractDtoResponse контракта с заданным id
     *
     * @param id - уникальный идентификатор контракта
     * @return ContractDtoResponse заданного контракта или пустой ContractDtoResponse, если данный контракт не существует
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<ContractDtoResponse> getContract(@PathVariable UUID id) {
        ContractDtoResponse contractById = contractService.getContractById(id);
        if ((contractById != null) && (contractById.getId() != null)) {
            return ResponseEntity.ok().body(contractById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Создает новый контракт для исполнителя {userId}
     *
     * @param contractDtoRequest - тело запроса на создание контракта
     * @return Тело запроса на создание контракта с уникальным проставленным id,
     * или тело запроса с id = null, если создать контракт не удалось
     */
    @PostMapping
    public ResponseEntity<? extends ContractBaseDto> createContract(@Validated @RequestBody ContractDtoRequest contractDtoRequest,
                                                                    UriComponentsBuilder componentsBuilder,
                                                                    BindingResult result) {

        if (result.hasErrors()) {
            contractDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(contractDtoRequest);
        }

        ContractDtoResponse contractDtoResponse = contractService.createContract(contractDtoRequest);

        if (contractDtoResponse != null) {
            URI uri = componentsBuilder
                    .path(String.format("/contracts/%s", contractDtoResponse.getId()))
                    .buildAndExpand(contractDtoResponse)
                    .toUri();

            return ResponseEntity.created(uri).body(contractDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }
    }


    /**
     * Обновляет существующий контракт {id} от имени пользователя {userId}
     *
     * @param id                 - уникальный идентификатор контракта
     * @param contractDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<? extends ContractBaseDto> updateContract(@Validated @RequestBody ContractDtoRequest contractDtoRequest,
                                                                    @PathVariable UUID id,
                                                                    BindingResult result) {

        if (result.hasErrors()) {
            contractDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(contractDtoRequest);
        }

        ContractDtoResponse contractDtoResponse = contractService.updateContract(id, contractDtoRequest);

        if (contractDtoResponse != null) {
            return ResponseEntity.ok().body(contractDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(contractDtoRequest);
        }
    }


    /**
     * Удаляет контракт с заданным id
     *
     * @param id - уникальный идентификатор контракта для удаления
     * @return - объект ContractDtoResponse с данными удаленного контракта
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ContractDtoResponse> deleteContract(@PathVariable UUID id) {
        if (contractService.deleteContract(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @InitBinder(value = "contractDtoRequest")
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(contractDtoRequestValidator);
    }

}
