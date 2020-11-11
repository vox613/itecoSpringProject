package ru.iteco.project.service;

import ru.iteco.project.controller.dto.TaskDtoRequest;
import ru.iteco.project.controller.dto.TaskDtoResponse;
import ru.iteco.project.model.Task;
import ru.iteco.project.model.TaskStatus;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности Task
 */
public interface TaskService {

    /**
     * Метод сохранения задания в коллекцию
     *
     * @param task - задание для сохраннения
     */
    void createTask(Task task);

    /**
     * Метод поиска задания по названию
     *
     * @param name - название задания
     * @return - список заданий, название которых совпадает с переданным
     */
    List<Task> findTaskByName(String name);

    /**
     * Метод удаления из коллекции переданного задания
     *
     * @param task - задание для удаления
     * @return - удаленное задание
     */
    Task deleteTask(Task task);

    /**
     * Метод изменения статуса задания на переданный в агументах
     *
     * @param task       - задание статус которого необходимо изменить
     * @param taskStatus - статус на которой меняется состояние задания
     */
    void changeTaskStatusTo(Task task, TaskStatus taskStatus);

    /**
     * Метод получает все задания из коллекции
     *
     * @return - список всех заданий из коллекции
     */
    List<TaskDtoResponse> getAllTasks();

    /**
     * Метод получает все задания пользователя с id из коллекции
     *
     * @return - список всех договоров пользователя из коллекции
     */
    List<TaskDtoResponse> getAllUserTasks(UUID userId);

    /**
     * Метод получения задания по его id
     *
     * @param id - уникальный идентификатор задания
     * @return TaskDtoResponse - dto объект с данными о задании
     */
    TaskDtoResponse getTaskById(UUID id);

    /**
     * Метод создания задания
     *
     * @param taskDtoRequest - тело запроса с данными для создания задания
     * @return TaskDtoRequest - dto объект с данными о задании
     */
    TaskDtoRequest createTask(TaskDtoRequest taskDtoRequest);

    /**
     * Метод обновления существующего задания
     *
     * @param id             - уникальный идентификатор задания
     * @param userId         - уникальный идентификатор Пользователя
     * @param taskDtoRequest -  тело запроса для обновления
     */
    void updateTask(UUID id, UUID userId, TaskDtoRequest taskDtoRequest);

    /**
     * Метод удаляет пользователя из коллекции
     *
     * @param id - id пользователя для удаления
     * @return - UserDto удаленного пользователя
     */
    TaskDtoResponse deleteTask(UUID id);

}
