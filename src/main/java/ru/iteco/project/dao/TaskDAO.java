package ru.iteco.project.dao;

import ru.iteco.project.model.Task;
import ru.iteco.project.model.TaskStatus;
import ru.iteco.project.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
     * Метод осуществляет поиск задания по имени
     *
     * @param name - имя задания
     * @return - список заданий с предоставленным именем
     */
    List<Task> findTaskByName(String name);

    /**
     * Метод осуществляет поиск всех заданий данного заказчика
     *
     * @param customer - заказчик, все задания которого необходимо найти
     * @return - список всех заданий заказчика
     */
    List<Task> findAllTasksByCustomer(User customer);

    /**
     * Метод осуществляет поиск всех выполненных заданий данного исполнителя
     *
     * @param executor - исполнитель, все выполненные задания которого необходимо найти
     * @return - список всех выполенных заданий исполнителя
     */
    List<Task> findAllTasksByExecutor(User executor);

    /**
     * Метод осуществляет поиск всех заданий данного заказчика
     *
     * @param customerId - id заказчика, все задания которого необходимо найти
     * @return - список всех заданий заказчика
     */
    List<Task> findAllTasksByCustomerId(UUID customerId);

    /**
     * Метод осуществляет поиск всех заданий с соответствующим статусом
     *
     * @param taskStatus - статус заданий, которые необходимо найти
     * @return - список всех заданий с предоставленным статусом
     */
    List<Task> findAllTasksByStatus(TaskStatus taskStatus);

    /**
     * Метод осуществляет поиск всех заданий, стоимость выполнения которых входит в переданный ценовой диапазон
     *
     * @param minPrice - нижний порог стоимости
     * @param maxPrice - верхний порог стоимости
     * @return - список всех заданий, стоимость исполнения которых входит в установленный диапазон
     */
    List<Task> findTasksInPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Метод осуществляет поиск всех заданий, срок выполнения которых позже, чем переданная дата
     *
     * @param localDateTime - пороговая дата относительно которой осуществляется поиск заданий
     * @return - список всех заданий срок выполнения которых находится позже переданной даты
     */
    List<Task> findAllTasksWithCompletionDateAfter(LocalDateTime localDateTime);

    /**
     * Метод проверяет существование задания с заданным id
     *
     * @param uuid - уникальный идентификатор задания
     * @return true - искомое задание существует, false - искомое задание не существует
     */
    boolean taskWithIdIsExist(UUID uuid);
}
