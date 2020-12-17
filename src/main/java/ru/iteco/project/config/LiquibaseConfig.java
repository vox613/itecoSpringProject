package ru.iteco.project.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

/**
 * Конфигурационный класс для настройки миграций базы данных с помощью Liquibase
 */
@Configuration
@DependsOn("dataSource")
public class LiquibaseConfig {

    @Bean
    SpringLiquibase getSpringLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/changelog-master.xml");
        return liquibase;
    }
}
