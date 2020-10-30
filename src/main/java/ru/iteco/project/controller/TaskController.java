package ru.iteco.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.iteco.project.controller.dto.TaskDtoRequest;
import ru.iteco.project.controller.dto.TaskDtoResponse;
import ru.iteco.project.service.TaskService;

import java.util.List;
import java.util.UUID;

@RestController
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @GetMapping("/tasks")
    List<TaskDtoResponse> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping(value = "/tasks/{id}")
    public TaskDtoResponse getTask(@PathVariable UUID id) {
        return taskService.getTaskById(id);
    }

    @PostMapping(value = "/tasks")
    public TaskDtoRequest createTask(@RequestBody TaskDtoRequest taskDtoRequest) {
        return taskService.createTask(taskDtoRequest);
    }

    @PutMapping(value = "/tasks/{id}")
    public void updateTask(@PathVariable UUID id, @RequestBody TaskDtoRequest taskDtoRequest) {
        taskService.updateTask(id, taskDtoRequest);
    }

    @DeleteMapping(value = "/tasks/{id}")
    public TaskDtoResponse deleteTask(@PathVariable UUID id) {
        return taskService.deleteTask(id);
    }


}
