package ru.iteco.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.iteco.project.domain.Task;
import ru.iteco.project.domain.TaskStatus;
import ru.iteco.project.domain.User;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static ru.iteco.project.domain.UserRole.UserRoleEnum.*;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности Task
 */
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    /**
     * Метод получения всех заданий заказчика по его id
     *
     * @param id - уникальный идентификатор заказчика
     * @return - коллекция заданий заказчика
     */
    Collection<Task> findTasksByCustomerId(UUID id);

    /**
     * Метод получения всех заданий исполнителя по его id
     *
     * @param id - уникальный идентификатор исполнителя
     * @return - коллекция заданий исполнителя
     */
    Collection<Task> findTasksByExecutorId(UUID id);

    /**
     * Метод получения всех заданий пользователя по его id
     *
     * @param user - сущность пользователя
     * @return - коллекция заданий пользователя или пустая коллекция если у пользователя нет заданий
     */
    default Collection<Task> findTasksByUser(User user) {
        if (isEqualsUserRole(CUSTOMER, user)) {
            return findTasksByCustomerId(user.getId());
        } else if (isEqualsUserRole(EXECUTOR, user)) {
            return findTasksByExecutorId(user.getId());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Метод получения всех заданий с переданным статусом
     *
     * @param taskStatus - статус задания
     * @return - коллекция заданий, с переданным статусом
     */
    Collection<Task> findAllByTaskStatus(TaskStatus taskStatus);

}
