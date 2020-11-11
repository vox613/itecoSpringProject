package ru.iteco.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteco.project.controller.dto.TaskDtoRequest;
import ru.iteco.project.controller.dto.TaskDtoResponse;
import ru.iteco.project.service.TaskService;
import ru.iteco.project.validator.TaskDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с Task
 */
@RestController
public class TaskController {

    /*** Объект сервисного слоя для Task*/
    private final TaskService taskService;

    /*** Объект валидатора для TaskDtoRequest*/
    private final TaskDtoRequestValidator taskDtoRequestValidator;


    @Autowired
    public TaskController(TaskService taskService, TaskDtoRequestValidator taskDtoRequestValidator) {
        this.taskService = taskService;
        this.taskDtoRequestValidator = taskDtoRequestValidator;
    }


    /**
     * Контроллер возвращает список всех заданий, а при наличии RequestParam {userId}
     * возвращаетс список заданий пользователя с соответствующим id
     *
     * @param userId - уникальный идентификатор пользователя
     * @return список объектов TaskDtoResponse
     */
    @GetMapping("/tasks")
    ResponseEntity<List<TaskDtoResponse>> getAllUserTasks(@RequestParam(required = false) UUID userId) {
        List<TaskDtoResponse> allTasks;
        if (userId != null) {
            allTasks = taskService.getAllUserTasks(userId);
        } else {
            allTasks = taskService.getAllTasks();
        }
        return ResponseEntity.ok().body(allTasks);
    }


    /**
     * Контроллер возвращает TaskDtoResponse задания с заданным id
     *
     * @param id - уникальный идентификатор задания
     * @return TaskDtoResponse заданного задания или пустой TaskDtoResponse, если данное задание не существует
     */
    @GetMapping(value = "/tasks/{id}")
    public ResponseEntity<TaskDtoResponse> getTask(@PathVariable UUID id) {
        TaskDtoResponse taskById = taskService.getTaskById(id);
        if (taskById.getId() != null) {
            return ResponseEntity.ok().body(taskById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Создает новое задание для заказчика
     *
     * @param taskDtoRequest - тело запроса на создание задания
     * @return Тело запроса на создание задания с уникальным проставленным id,
     * * или тело запроса с id = null, если создать задание не удалось
     */
    @PostMapping(value = "/tasks")
    public ResponseEntity<TaskDtoRequest> createTask(@Validated @RequestBody TaskDtoRequest taskDtoRequest,
                                                     BindingResult result,
                                                     UriComponentsBuilder componentsBuilder) {

        if (result.hasErrors()) {
            taskDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(taskDtoRequest);
        }

        TaskDtoRequest task = taskService.createTask(taskDtoRequest);

        URI uri = componentsBuilder
                .path(String.format("/tasks/%s", task.getId()))
                .buildAndExpand(task)
                .toUri();

        return ResponseEntity.created(uri).body(task);
    }


    /**
     * Обновляет существующее задание {id} от имени заказчика {userId}
     *
     * @param taskId         - уникальный идентификатор задания
     * @param userId         - уникальный идентификатор заказчика
     * @param taskDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "tasks/{taskId}")
    public ResponseEntity<TaskDtoRequest> updateTask(@PathVariable UUID taskId,
                                                     @RequestParam UUID userId,
                                                     @Validated @RequestBody TaskDtoRequest taskDtoRequest,
                                                     BindingResult result) {

        if (result.hasErrors()) {
            taskDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(taskDtoRequest);
        }

        taskService.updateTask(taskId, userId, taskDtoRequest);
        return ResponseEntity.ok().body(taskDtoRequest);
    }


    /**
     * Удаляет задание с заданным id
     *
     * @param id - уникальный идентификатор задания для удаления
     * @return - объект TaskDtoResponse с данными удаленного задания
     */
    @DeleteMapping(value = "/tasks/{id}")
    public ResponseEntity<TaskDtoResponse> deleteTask(@PathVariable UUID id) {
        TaskDtoResponse taskDtoResponse = taskService.deleteTask(id);
        if (taskDtoResponse.getId() != null) {
            return ResponseEntity.ok().body(taskDtoResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @InitBinder(value = "taskDtoRequest")
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(taskDtoRequestValidator);
    }

}
