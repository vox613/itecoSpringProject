package ru.iteco.project.service;

import ru.iteco.project.model.Task;
import ru.iteco.project.model.TaskStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности Task
 */
public interface TaskService {

    /**
     * Метод сохранения задания в коллекцию
     *
     * @param task - задание для сохраннения
     */
    void createTask(Task task);

    /**
     * Метод поиска задания по названию
     *
     * @param name - название задания
     * @return - список заданий, название которых совпадает с переданным
     */
    List<Task> findTaskByName(String name);

    /**
     * Метод удаления из коллекции переданного задания
     *
     * @param task - задание для удаления
     * @return - удаленное задание
     */
    Task deleteTask(Task task);

    /**
     * Метод изменения статуса задания на переданный в агументах
     *
     * @param task       - задание статус которого необходимо изменить
     * @param taskStatus - статус на которой меняется состояние задания
     */
    void changeTaskStatusTo(Task task, TaskStatus taskStatus);

    /**
     * Метод получает все задания из коллекции
     *
     * @return - список всех заданий из коллекции
     */
    ArrayList<Task> getAllTasks();

}
