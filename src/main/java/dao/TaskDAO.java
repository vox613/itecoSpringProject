package dao;

import model.Task;
import model.TaskStatus;
import model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskDAO extends GenericDAO<Task, UUID> {

    Task findTaskById(UUID taskId);

    Task findTaskByName(String name);

    List<Task> findAllTasksByCustomer(User customer);

    List<Task> findAllTasksByStatus(TaskStatus taskStatus);

    List<Task> findTasksInPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    List<Task> findAllTasksWithCompletionDateAfter(LocalDateTime localDateTime);

}