package service;

import dao.TaskDAO;
import model.Task;
import model.TaskStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class TaskServiceImpl implements TaskService {

    private static final Logger log = LogManager.getLogger(TaskServiceImpl.class.getName());

    private TaskDAO taskDAO;

    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    @Override
    public void createTask(Task task) {
        taskDAO.save(task);
        log.info("now: " + LocalDateTime.now() + " createTask: " + task);
    }

    @Override
    public Task findTaskByName(String name) {
        Task taskByName = taskDAO.findTaskByName(name);
        log.info("now: " + LocalDateTime.now() + " findTaskByName: " + taskByName);
        return taskByName;
    }

    @Override
    public Task deleteTask(Task task) {
        Task deletedTask = taskDAO.delete(task);
        log.info("now: " + LocalDateTime.now() + " deleteTask: " + deletedTask);
        return deletedTask;
    }

    @Override
    public void changeTaskStatusTo(Task task, TaskStatus taskStatus) {
        task.setTaskStatus(taskStatus);
        taskDAO.update(task);
        log.info("now: " + LocalDateTime.now() + " changeTaskStatusTo: " + task + "StatusTo: " + taskStatus);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskDAO.getAll());
    }

    public TaskDAO getTaskDAO() {
        return taskDAO;
    }
}
