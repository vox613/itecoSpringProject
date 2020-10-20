package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.iteco.project.dao.TaskDAO;
import ru.iteco.project.model.Task;
import ru.iteco.project.model.TaskStatus;
import ru.iteco.project.service.validators.CustomValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс реализующий функционал сервисного слоя для работы с заданиями
 */
@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LogManager.getLogger(TaskServiceImpl.class.getName());

    private TaskDAO taskDAO;
    private CustomValidator<Task> taskValidator;

    @Autowired
    public void setTaskValidator(CustomValidator<Task> taskValidator) {
        this.taskValidator = taskValidator;
    }

    /**
     * Метод сохранения задания в коллекцию
     *
     * @param task - задание для сохраннения
     */
    @Override
    public void createTask(Task task) {
        taskValidator.validate(task);
        taskDAO.save(task);
        log.info("now: " + LocalDateTime.now() + " createTask: " + task);
    }

    /**
     * Метод поиска задания по названию
     *
     * @param name - название задания
     * @return - список заданий, название которых совпадает с переданным
     */
    @Override
    public List<Task> findTaskByName(String name) {
        List<Task> taskList = taskDAO.findTaskByName(name);
        log.info("now: " + LocalDateTime.now() + " findTaskByName: " + taskList);
        return taskList;
    }

    /**
     * Метод удаления из коллекции переданного задания
     *
     * @param task - задание для удаления
     * @return - удаленное задание
     */
    @Override
    public Task deleteTask(Task task) {
        Task deletedTask = taskDAO.delete(task);
        log.info("now: " + LocalDateTime.now() + " deleteTask: " + deletedTask);
        return deletedTask;
    }

    /**
     * Метод изменения статуса задания на переданный в агументах
     *
     * @param task       - задание статус которого необходимо изменить
     * @param taskStatus - статус на которой меняется состояние задания
     */
    @Override
    public void changeTaskStatusTo(Task task, TaskStatus taskStatus) {
        task.setTaskStatus(taskStatus);
        taskDAO.update(task);
        log.info("now: " + LocalDateTime.now() + " changeTaskStatusTo: " + task + "StatusTo: " + taskStatus);
    }

    /**
     * Метод получает все задания из коллекции
     *
     * @return - список всех заданий из коллекции
     */
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskDAO.getAll());
    }

    public TaskDAO getTaskDAO() {
        return taskDAO;
    }

    @Autowired
    @Qualifier("taskDao")
    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public CustomValidator<Task> getTaskValidator() {
        return taskValidator;
    }
}