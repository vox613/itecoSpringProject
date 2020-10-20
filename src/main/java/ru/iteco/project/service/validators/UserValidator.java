package ru.iteco.project.service.validators;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.iteco.project.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@PropertySource(value = {"classpath:application.properties"})
public class UserValidator implements CustomValidator<User> {

    @Value("${user.email.regexp}")
    private String emailRegExpValidator;

    @Value("${user.phone.regexp}")
    private String phoneRegExpValidator;


    /**
     * Валидатор полей для сущности пользователя
     *
     * @param user - объект пользователя
     */
    @Override
    public void validate(User user) {
        if (Objects.isNull(user)) {
            throw new RuntimeException("Попытка добавить пустого пользователя");
        }
        emailValidate(user.getEmail());
        phoneValidate(user.getPhoneNumber());
    }

    private void emailValidate(String email) {
        if (Strings.isEmpty(email) || !email.matches(emailRegExpValidator)) {
            log.error("now: " + LocalDateTime.now() + " - Регистрация пользователя без email невозможна! Пользователь не создан!");
            throw new RuntimeException("Некорректный email, пользователь не создан!");
        }
    }

    private void phoneValidate(String phone) {
        if (Strings.isEmpty(phone) || !phone.matches(phoneRegExpValidator)) {
            log.error("now: " + LocalDateTime.now() + " - Регистрация пользователя без phone невозможна! Пользователь не создан!");
            throw new RuntimeException("Некорректный phone, пользователь не создан!");
        }
    }

}
