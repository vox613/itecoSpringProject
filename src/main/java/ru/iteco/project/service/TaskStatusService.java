package ru.iteco.project.service;


import ru.iteco.project.controller.dto.TaskStatusDtoRequest;
import ru.iteco.project.controller.dto.TaskStatusDtoResponse;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности TaskStatus
 */
public interface TaskStatusService {

    /**
     * Метод получения статуса задания по id
     *
     * @param id - уникальный идентификатор статуса задания
     * @return - представление данных статуса задания в форме TaskRoleDtoResponse
     */
    TaskStatusDtoResponse getTaskStatusById(UUID id);

    /**
     * Метод создания нового статуса задания
     *
     * @param taskStatusDtoRequest - запрос с данными о статусе задания
     * @return - объект UserRoleDtoResponse с уникальным идентификатором id
     */
    TaskStatusDtoResponse createTaskStatus(TaskStatusDtoRequest taskStatusDtoRequest);

    /**
     * Метод обновления данных статуса задания
     *
     * @param id                   - уникальный идентификатор статуса задания
     * @param taskStatusDtoRequest - запрос с обновленными данными статуса
     * @return - объект TaskStatusDtoResponse с обновленной сущностью задания
     */
    TaskStatusDtoResponse updateTaskStatus(UUID id, TaskStatusDtoRequest taskStatusDtoRequest);

    /**
     * Метод получения данных обо всех статусах задания
     *
     * @return - список всех статусов задания в форме TaskRoleDtoResponse
     */
    ArrayList<TaskStatusDtoResponse> getAllTasksStatuses();

    /**
     * Метод удаляет статус задания
     *
     * @param id - id статуса задания для удаления
     * @return - true - статус задания успешно удалена,
     * false - произошла ошибка при удалении статуса задания или статуса задания не существует
     */
    Boolean deleteTaskStatus(UUID id);

}
