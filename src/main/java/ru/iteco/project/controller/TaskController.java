package ru.iteco.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.iteco.project.controller.dto.TaskDtoRequest;
import ru.iteco.project.controller.dto.TaskDtoResponse;
import ru.iteco.project.service.TaskService;

import java.util.List;
import java.util.UUID;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с Task
 */
@RestController
public class TaskController {

    /**
     * Объект сервисного слоя для Task
     */
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Контроллер возвращает список всех созданных заданий
     *
     * @return - список TaskDtoResponse
     */
    @GetMapping("/tasks")
    List<TaskDtoResponse> getAllTasks() {
        return taskService.getAllTasks();
    }


    /**
     * Контроллер возвращает список всех заданий пользователя {userId}
     *
     * @param userId - уникальный идентификатор пользователя
     * @return список объектов TaskDtoResponse
     */
    @GetMapping("/users/{userId}/tasks")
    List<TaskDtoResponse> getAllUserTasks(@PathVariable UUID userId) {
        return taskService.getAllUserTasks(userId);
    }

    /**
     * Контроллер возвращает TaskDtoResponse задания с заданным id
     *
     * @param id - уникальный идентификатор задания
     * @return TaskDtoResponse заданного задания или пустой TaskDtoResponse, если данное задание не существует
     */
    @GetMapping(value = "/tasks/{id}")
    public TaskDtoResponse getTask(@PathVariable UUID id) {
        return taskService.getTaskById(id);
    }

    /**
     * Создает новое задание для заказчика {userId}
     *
     * @param userId         - уникальный идентификатор заказчика
     * @param taskDtoRequest - тело запроса на создание задания
     * @return Тело запроса на создание задания с уникальным проставленным id,
     * или тело запроса с id = null, если создать задание не удалось
     */
    @PostMapping(value = "/users/{userId}/tasks")
    public TaskDtoRequest createTask(@RequestBody TaskDtoRequest taskDtoRequest, @PathVariable UUID userId) {
        return taskService.createTask(userId, taskDtoRequest);
    }


    /**
     * Создает новое задание для заказчика
     *
     * @param taskDtoRequest - тело запроса на создание задания
     * @return Тело запроса на создание задания с уникальным проставленным id,
     * * или тело запроса с id = null, если создать задание не удалось
     */
    @PostMapping(value = "/tasks")
    public TaskDtoRequest createTask(@RequestBody TaskDtoRequest taskDtoRequest) {
        return taskService.createTask(taskDtoRequest);
    }

    /**
     * Обновляет существующее задание {id} от имени заказчика {userId}
     *
     * @param taskId         - уникальный идентификатор задания
     * @param userId         - уникальный идентификатор заказчика
     * @param taskDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/users/{userId}/tasks/{taskId}")
    public void updateTask(@PathVariable UUID taskId, @PathVariable UUID userId, @RequestBody TaskDtoRequest taskDtoRequest) {
        taskService.updateTask(taskId, userId, taskDtoRequest);
    }

    /**
     * Удаляет задание с заданным id
     *
     * @param id - уникальный идентификатор задания для удаления
     * @return - объект TaskDtoResponse с данными удаленного задания
     */
    @DeleteMapping(value = "/tasks/{id}")
    public TaskDtoResponse deleteTask(@PathVariable UUID id) {
        return taskService.deleteTask(id);
    }


}
