package ru.iteco.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.service.UserService;
import ru.iteco.project.validator.UserDtoRequestValidator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с User
 */
@RestController
public class UserController {

    /*** Объект сервисного слоя для User*/
    private final UserService userService;

    /*** Объект валидатора для UserDtoRequest*/
    private final UserDtoRequestValidator userDtoRequestValidator;


    @Autowired
    public UserController(UserService userService, UserDtoRequestValidator userDtoRequestValidator) {
        this.userService = userService;
        this.userDtoRequestValidator = userDtoRequestValidator;
    }


    /**
     * Контроллер возвращает список всех созданных пользователей
     *
     * @return - список UserDtoResponse
     */
    @GetMapping("/users")
    ResponseEntity<List<UserDtoResponse>> getAllUsers() {
        ArrayList<UserDtoResponse> allUsers = userService.getAllUsers();
        return ResponseEntity.ok().body(allUsers);
    }


    /**
     * Контроллер возвращает UserDtoResponse пользователя с заданным id
     *
     * @param id - уникальный идентификатор пользователя
     * @return ContractDtoResponse заданного пользователя или пустой ContractDtoResponse, если данный пользователь не существует
     */
    @GetMapping(value = "/users/{id}")
    public ResponseEntity<UserDtoResponse> getUser(@PathVariable UUID id) {
        UserDtoResponse userById = userService.getUserById(id);
        if (userById.getId() != null) {
            return ResponseEntity.ok().body(userById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Создает нового пользователя
     *
     * @param userDtoRequest - тело запроса на создание пользователя
     * @return Тело запроса на создание пользователя с уникальным проставленным id,
     * * или тело запроса с id = null, если создать пользователя не удалось
     */
    @PostMapping(value = "/users")
    public ResponseEntity<UserDtoRequest> createUser(@Validated @RequestBody UserDtoRequest userDtoRequest,
                                                     BindingResult result,
                                                     UriComponentsBuilder componentsBuilder) {
        if (result.hasErrors()) {
            userDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userDtoRequest);
        }

        UserDtoRequest user = userService.createUser(userDtoRequest);
        URI uri = componentsBuilder.path("/users/" + user.getId()).buildAndExpand(user).toUri();
        return ResponseEntity.created(uri).body(user);
    }


    /**
     * Обновляет существующего пользователя {id}
     *
     * @param id             - уникальный идентификатор пользователя
     * @param userDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/users/{id}")
    public ResponseEntity<UserDtoRequest> updateUser(@PathVariable UUID id,
                                                     @Validated @RequestBody UserDtoRequest userDtoRequest,
                                                     BindingResult result) {

        if (result.hasErrors()) {
            userDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userDtoRequest);
        }

        userService.updateUser(id, userDtoRequest);
        return ResponseEntity.ok().body(userDtoRequest);
    }


    /**
     * Удаляет пользователя с заданным id
     *
     * @param id - уникальный идентификатор пользователя для удаления
     * @return - объект UserDtoResponse с данными удаленного пользователя
     */
    @DeleteMapping(value = "/users/{id}")
    public ResponseEntity<UserDtoResponse> deleteUser(@PathVariable UUID id) {
        UserDtoResponse userDtoResponse = userService.deleteUser(id);
        if (userDtoResponse.getId() != null) {
            return ResponseEntity.ok().body(userDtoResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @InitBinder(value = "userDtoRequest")
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(userDtoRequestValidator);
    }

}
