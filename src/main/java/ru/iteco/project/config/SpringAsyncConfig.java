package ru.iteco.project.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Класс-конфиг для подключения возможности работы с асинхронными методами
 */
@Configuration
@EnableAsync
public class SpringAsyncConfig implements AsyncConfigurer {

    @Value("${spring.async.threadPool.corePoolSize}")
    private String corePoolSize;

    @Value("${spring.async.threadPool.maxPoolSize}")
    private String maxPoolSize;



    /**
     * Метод получения пула потоков
     * @return пул потоков для выполнения задач
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(Integer.parseInt(corePoolSize));
        threadPoolTaskExecutor.setMaxPoolSize(Integer.parseInt(maxPoolSize));
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    /**
     * Перехватчик исключений, возникших во время асинхронного процесса
     * @return новый объект перехватчика асинхронных исключений
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

}
