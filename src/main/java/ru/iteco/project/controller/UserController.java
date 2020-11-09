package ru.iteco.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.service.UserService;

import java.util.List;
import java.util.UUID;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с User
 */
@RestController
public class UserController {

    /**
     * Объект сервисного слоя для User
     */
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Контроллер возвращает список всех созданных пользователей
     *
     * @return - список UserDtoResponse
     */
    @GetMapping("/users")
    List<UserDtoResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Контроллер возвращает UserDtoResponse пользователя с заданным id
     *
     * @param id - уникальный идентификатор пользователя
     * @return ContractDtoResponse заданного пользователя или пустой ContractDtoResponse, если данный пользователь не существует
     */
    @GetMapping(value = "/users/{id}")
    public UserDtoResponse getUser(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    /**
     * Создает нового пользователя
     *
     * @param userDtoRequest - тело запроса на создание пользователя
     * @return Тело запроса на создание пользователя с уникальным проставленным id,
     * * или тело запроса с id = null, если создать пользователя не удалось
     */
    @PostMapping(value = "/users")
    public UserDtoRequest createUser(@RequestBody UserDtoRequest userDtoRequest) {
        return userService.createUser(userDtoRequest);
    }

    /**
     * Обновляет существующего пользователя {id}
     *
     * @param id             - уникальный идентификатор пользователя
     * @param userDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/users/{id}")
    public void updateUser(@PathVariable UUID id, @RequestBody UserDtoRequest userDtoRequest) {
        userService.updateUser(id, userDtoRequest);
    }


    /**
     * Удаляет пользователя с заданным id
     *
     * @param id - уникальный идентификатор пользователя для удаления
     * @return - объект UserDtoResponse с данными удаленного пользователя
     */
    @DeleteMapping(value = "/users/{id}")
    public UserDtoResponse deleteUser(@PathVariable UUID id) {
        return userService.deleteUser(id);
    }

}
