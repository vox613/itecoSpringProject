package ru.iteco.project.dao.map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.iteco.project.dao.ContractDAO;
import ru.iteco.project.dao.TaskDAO;
import ru.iteco.project.dao.UserDAO;

/**
 * Класс-конфигурация бинов для DAO слоя
 */
@Configuration
public class RepositoryConfig {

    /**
     * Метод получения бина для доступа к данным о пользователях
     *
     * @return - реализацию бина для доступа к данным о пользователях
     */
    @Bean(name = "userDao")
    public UserDAO getUserDao() {
        return new UserDAOImpl();
    }

    /**
     * Метод получения бина для доступа к данным о заданиях
     *
     * @return - реализацию бина для доступа к данным о заданиях
     */
    @Bean(name = "taskDao")
    public TaskDAO getTaskDao() {
        return new TaskDAOImpl();
    }

    /**
     * Метод получения бина для доступа к данным о договорах
     *
     * @return - реализацию бина для доступа к данным о договорах
     */
    @Bean(name = "contractDao")
    public ContractDAO getContractDao() {
        return new ContractDAOImpl();
    }


}
