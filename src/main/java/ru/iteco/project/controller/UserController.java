package ru.iteco.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.iteco.project.controller.dto.UserDto;
import ru.iteco.project.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
public class UserController {


    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/users")
    List<UserDto> getAllUsers() {
        return userService.getAllDtoUsers();
    }

    @GetMapping(value = "/users/{id}")
    public UserDto getUser(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @PostMapping(value = "/users")
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PutMapping(value = "/users/{id}")
    public UserDto updateUser(@PathVariable UUID id, @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping(value = "/users/{id}")
    public UserDto deleteUser(@PathVariable UUID id) {
        return userService.deleteUser(id);
    }

}
