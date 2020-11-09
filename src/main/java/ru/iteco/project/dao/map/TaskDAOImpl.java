package ru.iteco.project.dao.map;

import org.springframework.stereotype.Repository;
import ru.iteco.project.dao.TaskDAO;
import ru.iteco.project.model.Task;
import ru.iteco.project.model.TaskStatus;
import ru.iteco.project.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс реализующий функционал доступа к данным о заданиях
 */
@Repository
public class TaskDAOImpl extends AbstractDao<Task, UUID> implements TaskDAO {


    public TaskDAOImpl() {
        super(Task.class, new HashMap<>());
    }

    /**
     * Метод осуществляет поиск задания по уникальному id
     *
     * @param taskId - уникальный id задания
     * @return - объект задания, соответствующий данному id, или null, если задания нет в коллекции
     */
    @Override
    public Optional<Task> findTaskById(UUID taskId) {
        return Optional.ofNullable(getByPK(taskId));
    }

    /**
     * Метод осуществляет поиск задания по имени
     *
     * @param name - имя задания
     * @return - список заданий с предоставленным именем
     */
    @Override
    public List<Task> findTaskByName(String name) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : elements.values()) {
            if ((task != null) && task.getName().equals(name)) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Метод осуществляет поиск всех заданий данного заказчика
     *
     * @param customer - исполнитель, все задания которого необходимо найти
     * @return - список всех заданий заказчика
     */
    @Override
    public List<Task> findAllTasksByCustomer(User customer) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : elements.values()) {
            if ((task != null) && task.getCustomer().getId().equals(customer.getId())) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    @Override
    public List<Task> findAllTasksByExecutor(User executor) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : elements.values()) {
            if ((task != null) && (task.getExecutor() != null)
                    && task.getExecutor().getId().equals(executor.getId())) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    @Override
    public List<Task> findAllTasksByCustomerId(UUID customerId) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : elements.values()) {
            if ((task != null) && task.getCustomer().getId().equals(customerId)) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Метод осуществляет поиск всех заданий с соответствующим статусом
     *
     * @param taskStatus - статус заданий, которые необходимо найти
     * @return - список всех заданий с предоставленным статусом
     */
    @Override
    public List<Task> findAllTasksByStatus(TaskStatus taskStatus) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : elements.values()) {
            if ((task != null) && task.getTaskStatus().equals(taskStatus)) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Метод осуществляет поиск всех заданий, стоимость выполнения которых входит в переданный ценовой диапазон
     *
     * @param minPrice - нижний порог стоимости
     * @param maxPrice - верхний порог стоимости
     * @return - список всех заданий, стоимость исполнения которых входит в установленный диапазон
     */
    @Override
    public List<Task> findTasksInPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : elements.values()) {
            BigDecimal price = task.getPrice();
            if ((price != null) && (price.compareTo(minPrice) >= 0) && (price.compareTo(maxPrice) <= 0)) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Метод осуществляет поиск всех заданий, срок выполнения которых позже, чем переданная дата
     *
     * @param localDateTime - пороговая дата относительно которой осуществляется поиск заданий
     * @return - список всех заданий срок выполнения которых находится позже переданной даты
     */
    @Override
    public List<Task> findAllTasksWithCompletionDateAfter(LocalDateTime localDateTime) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : elements.values()) {
            LocalDateTime taskCompletionDate = task.getTaskCompletionDate();
            if ((taskCompletionDate != null) && (taskCompletionDate.isAfter(localDateTime))) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    @Override
    public boolean taskWithIdIsExist(UUID uuid) {
        return Optional.ofNullable(getByPK(uuid)).isPresent();
    }
}
