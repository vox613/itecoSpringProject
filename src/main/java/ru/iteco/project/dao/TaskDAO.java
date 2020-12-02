package ru.iteco.project.dao;

import ru.iteco.project.model.Task;
import ru.iteco.project.model.TaskStatus;
import ru.iteco.project.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал DAO слоя для сущности Task
 */
public interface TaskDAO extends GenericDAO<Task, UUID> {

    /**
     * Метод осуществляет поиск задания по уникальному id
     *
     * @param taskId - уникальный id задания
     * @return - объект задания, соответствующий данному id, или null, если задания нет в коллекции
     */
    Optional<Task> findTaskById(UUID taskId);

    /**
     * Метод осуществляет поиск всех заданий данного пользователя
     *
     * @param user - пользователь, все задания которого необходимо найти
     * @return - список всех заданий пользователя
     */
    Collection<Task> findAllTasksByUser(User user);

    /**
     * Метод осуществляет поиск всех заданий данного заказчика
     *
     * @param customerId - id заказчика, все задания которого необходимо найти
     * @return - список всех заданий заказчика
     */
    Collection<Task> findAllTasksByCustomerId(UUID customerId);

    /**
     * Метод осуществляет поиск всех заданий данного исполнителя
     *
     * @param executorId - id исполнителя, все задания которого необходимо найти
     * @return - список всех заданий исполнителя
     */
    Collection<Task> findAllTasksByExecutorId(UUID executorId);

    /**
     * Метод осуществляет поиск всех заданий c переданным статусом задания
     *
     * @param taskStatus - статус задания
     * @return - список всех заданий с переданным статусом
     */
    Collection<Task> findAllTasksWithStatus(TaskStatus taskStatus);

    /**
     * Метод осуществляет поиск всех заданий c переданным статусом задания
     *
     * @param taskStatusId - id статуса задания
     * @return - список всех заданий с переданным статусом
     */
    Collection<Task> findAllTasksWithStatusId(UUID taskStatusId);

    /**
     * Метод проверяет существование задания с заданным id
     *
     * @param uuid - уникальный идентификатор задания
     * @return true - искомое задание существует, false - искомое задание не существует
     */
    boolean taskWithIdIsExist(UUID uuid);

    /**
     * Метод обновления задания с установкой переданного статуса
     *
     * @param task           - сущность задания
     * @param taskStatusEnum - элеммент перечисления статусов задания
     * @return - Optional с обновленным объектом Task
     */
    Optional<Task> updateTaskStatus(Task task, TaskStatus.TaskStatusEnum taskStatusEnum);
}
