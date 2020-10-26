package ru.iteco.project.service.validators;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.iteco.project.model.Task;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс реализует функционал валидации полей сущности Task
 * согласно ограничениям заданным в файле application.properties
 */
@Service
@PropertySource(value = {"classpath:application.properties"})
public class TaskValidator implements CustomValidator<Task> {

    @Value("${task.nameMaxLength}")
    private String maxLengthTaskName;

    @Value("${task.minPrice}")
    private String minPriceTask;

    /**
     * Валидатор полей для сущности Задание
     *
     * @param task - объект задания
     */
    @Override
    public void validate(Task task) {
        if (Objects.isNull(task)) {
            throw new RuntimeException("Попытка добавить пустое задание");
        }
        nameValidate(task.getName());
        priceValidate(task.getPrice());
    }

    private void nameValidate(String taskName) {
        if (Strings.isEmpty(taskName) || (taskName.length() > Integer.parseInt(maxLengthTaskName))) {
            log.error("now: " + LocalDateTime.now() + " - Название задания не может быть пустым или превышать в длине "
                    + maxLengthTaskName + " символов! taskName = " + taskName);
            throw new RuntimeException("Некорректное название задания! Задание не создано!");
        }
    }

    private void priceValidate(BigDecimal taskPrice) {
        if (Objects.isNull(taskPrice) || (new BigDecimal(minPriceTask).compareTo(taskPrice) > 0)) {
            log.error("now: " + LocalDateTime.now() + " - Стоимость задания не может быть пустой или быть менее" +
                    minPriceTask + "! taskPrice = " + taskPrice);
            throw new RuntimeException("Некорректная стоимость задания! Задание не создано!");
        }
    }

}
