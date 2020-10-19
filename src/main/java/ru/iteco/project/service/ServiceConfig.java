package ru.iteco.project.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.iteco.project.dao.UserDAO;

/**
 * Класс-конфигурация бинов для сервисного слоя
 */
@Configuration
public class ServiceConfig {

    /**
     * Метод получения бина для работы с данными о пользователях
     *
     * @return - реализацию бина для работы с данным о пользователях
     */
    @Bean(name = "userService")
    public UserService getUserService(@Qualifier("userDao") UserDAO userDAO) {
        return new UserServiceImpl(userDAO);
    }

    /**
     * Метод получения бина для работы с данными о заданиях
     *
     * @return - реализацию бина для работы с данными о заданиях
     */
    @Bean(name = "taskService")
    public TaskService getTaskService() {
        return new TaskServiceImpl();
    }

    /**
     * Метод получения бина для работы с данным о договорах
     *
     * @return - реализацию бина для работы с данными о договорах
     */
    @Bean(name = "contractService")
    public ContractService getContractService() {
        return new ContractServiceImpl();
    }

}
