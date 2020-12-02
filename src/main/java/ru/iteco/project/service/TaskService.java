package ru.iteco.project.service;

import ru.iteco.project.controller.dto.TaskDtoRequest;
import ru.iteco.project.controller.dto.TaskDtoResponse;
import ru.iteco.project.model.Task;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности Task
 */
public interface TaskService {

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
     * @return TaskDtoResponse - dto объект с данными о задании
     */
    TaskDtoResponse createTask(TaskDtoRequest taskDtoRequest);

    /**
     * Метод обновления существующего задания
     *
     * @param id             - уникальный идентификатор задания
     * @param taskDtoRequest -  тело запроса для обновления
     * @return TaskDtoResponse - dto объект с данными о задании
     */
    TaskDtoResponse updateTask(UUID id, TaskDtoRequest taskDtoRequest);

    /**
     * Метод удаляет пользователя из коллекции
     *
     * @param id - id пользователя для удаления
     * @return - true - задание успешно удалено,
     * false - произошла ошибка при удалении задания/задания не существует
     */
    Boolean deleteTask(UUID id);

    /**
     * Метод формирует ответ TaskDtoResponse и обогащает его данными о заказчике и исполнителе
     *
     * @param task - объект задания
     * @return - объект TaskDtoResponse с подготовленными данными о задании, исполнителе и заказчике
     */
    TaskDtoResponse enrichByUsersInfo(Task task);
}
