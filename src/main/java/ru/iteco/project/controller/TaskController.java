package ru.iteco.project.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteco.project.controller.dto.TaskBaseDto;
import ru.iteco.project.controller.dto.TaskDtoRequest;
import ru.iteco.project.controller.dto.TaskDtoResponse;
import ru.iteco.project.exception.MismatchedIdException;
import ru.iteco.project.service.TaskService;
import ru.iteco.project.validator.TaskDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с Task
 */
@RestController
@RequestMapping(value = "/api/v1/tasks")
@PropertySource(value = {"classpath:errors.properties"})
public class TaskController {

    /*** Объект сервисного слоя для Task*/
    private final TaskService taskService;

    /*** Объект валидатора для TaskDtoRequest*/
    private final TaskDtoRequestValidator taskDtoRequestValidator;

    @Value("${errors.id.mismatched}")
    private String mismatchedIdMessage;


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
    @GetMapping
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
    @GetMapping(value = "/{id}")
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
    @PostMapping
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
     * @param id             - уникальный идентификатор задания
     * @param taskDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<? extends TaskBaseDto> updateTask(@PathVariable UUID id,
                                                            @Validated @RequestBody TaskDtoRequest taskDtoRequest,
                                                            BindingResult result) {

        if (result.hasErrors()) {
            taskDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(taskDtoRequest);
        }

        if (!Objects.equals(id, taskDtoRequest.getId())) {
            throw new MismatchedIdException(mismatchedIdMessage);
        }

        TaskDtoResponse taskDtoResponse = taskService.updateTask(id, taskDtoRequest);

        if (taskDtoResponse != null) {
            return ResponseEntity.ok().body(taskDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(taskDtoRequest);
        }
    }


    /**
     * Удаляет задание с заданным id
     *
     * @param id - уникальный идентификатор задания для удаления
     * @return - объект TaskDtoResponse с данными удаленного задания
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteTask(@PathVariable UUID id) {
        if (taskService.deleteTask(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @InitBinder(value = "taskDtoRequest")
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(taskDtoRequestValidator);
    }

}
