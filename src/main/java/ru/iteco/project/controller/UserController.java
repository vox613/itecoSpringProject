package ru.iteco.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
public class UserController {


    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/users")
    List<UserDtoResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(value = "/users/{id}")
    public UserDtoResponse getUser(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @PostMapping(value = "/users")
    public UserDtoRequest createUser(@RequestBody UserDtoRequest userDtoRequest) {
        return userService.createUser(userDtoRequest);
    }

    @PutMapping(value = "/users/{id}")
    public void updateUser(@PathVariable UUID id, @RequestBody UserDtoRequest userDtoRequest) {
        userService.updateUser(id, userDtoRequest);
    }

    @DeleteMapping(value = "/users/{id}")
    public UserDtoResponse deleteUser(@PathVariable UUID id) {
        return userService.deleteUser(id);
    }

}
