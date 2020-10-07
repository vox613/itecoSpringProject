package service;

import model.Task;
import model.TaskStatus;

import java.util.ArrayList;

public interface TaskService {

    void createTask(Task task);

    Task findTaskByName(String name);

    Task deleteTask(Task task);

    void changeTaskStatusTo(Task task, TaskStatus taskStatus);

    ArrayList<Task> getAllTasks();

}
