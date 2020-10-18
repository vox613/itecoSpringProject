package ru.iteco.project.dao.map;

import ru.iteco.project.dao.TaskDAO;
import ru.iteco.project.model.Task;
import ru.iteco.project.model.TaskStatus;
import ru.iteco.project.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TaskDAOImpl extends AbstractDao<Task, UUID> implements TaskDAO {


    public TaskDAOImpl() {
        super(Task.class, new HashMap<>());
    }

    @Override
    public Task findTaskById(UUID taskId) {
        return getByPK(taskId);
    }

    @Override
    public Task findTaskByName(String name) {
        for (Task task : elements.values()) {
            if ((task != null) && task.getName().equals(name)) {
                return task;
            }
        }
        return null;
    }

    @Override
    public List<Task> findAllTasksByCustomer(User customer) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : elements.values()) {
            if ((task != null) && task.getCustomer().equals(customer)) {
                tasks.add(task);
            }
        }
        return tasks;
    }

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
}
