package ru.iteco.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;

/**
 * Конфигурационный класс для datasource базы данных
 */
@Configuration
public class SpringJDBCConfig {

    /**
     * Создание бина jdbcTemplate для получения функционала JdbcTemplate
     *
     * @param dataSource - источник данных
     * @return - сконфигурированный бин jdbcTemplate
     */
    @Bean
    @Scope("prototype")
    @DependsOn("dataSource")
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    /**
     * Создание бина namedParameterJdbcTemplate для получения функционала NamedParameterJdbcTemplate
     *
     * @param dataSource - источник данных
     * @return - сконфигурированный бин namedParameterJdbcTemplate
     */
    @Bean
    @Scope("prototype")
    @DependsOn("dataSource")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }


    /**
     * Создание бина simpleJdbcInsert для получения функционала SimpleJdbcInsert
     *
     * @param dataSource - источник данных
     * @return - сконфигурированный бин simpleJdbcInsert
     */
    @Bean
    @Scope("prototype")
    @DependsOn("dataSource")
    public SimpleJdbcInsert simpleJdbcInsert(DataSource dataSource) {
        return new SimpleJdbcInsert(dataSource);
    }

}
