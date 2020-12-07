package ru.iteco.project.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.model.Role;
import ru.iteco.project.model.UserStatus;

/**
 * Класс содержит валидаторы для полей объекта запроса UserDtoRequest
 */
@Component
@PropertySource(value = {"classpath:application.properties"})
public class UserDtoRequestValidator extends AbstractDtoValidator implements Validator {
    private static final Logger logger = LogManager.getLogger(UserDtoRequestValidator.class.getName());

    @Value("${user.email.regexp}")
    private String emailRegExpValidator;

    public UserDtoRequestValidator(MessageSource messageSource) {
        super(messageSource);
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return UserDtoRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        UserDtoRequest userForm = (UserDtoRequest) target;

        if (StringUtils.isEmpty(userForm.getFirstName())) {
            logger.error("firstName is empty");
            prepareErrorMessage(errors, "user.firstName.empty", "firstName");
        }
        if (errors.hasErrors()) return;


        if (StringUtils.isEmpty(userForm.getSecondName())) {
            logger.error("secondName is empty");
            prepareErrorMessage(errors, "user.secondName.empty", "secondName");
        }
        if (errors.hasErrors()) return;


        if (StringUtils.isEmpty(userForm.getLogin())) {
            logger.error("login is empty");
            prepareErrorMessage(errors, "user.login.empty", "login");
        }
        if (errors.hasErrors()) return;


        if (StringUtils.isEmpty(userForm.getEmail())) {
            logger.error("email is empty");
            prepareErrorMessage(errors, "user.email.empty", "email");
        } else if (!userForm.getEmail().matches(emailRegExpValidator)) {
            logger.error("email is incorrect");
            prepareErrorMessage(errors, "user.email.incorrect", "email");
        }
        if (errors.hasErrors()) return;


        if (StringUtils.isEmpty(userForm.getRole())) {
            logger.error("role is empty");
            prepareErrorMessage(errors, "user.role.empty", "role");
        } else if (!Role.isCorrectValue(userForm.getRole())) {
            logger.error("role is incorrect");
            prepareErrorMessage(errors, "user.role.incorrect", "role");
        }
        if (errors.hasErrors()) return;


        if (userForm.getWallet() == null) {
            logger.error("wallet is empty");
            prepareErrorMessage(errors, "user.wallet.empty", "wallet");
        } else if (userForm.getWallet().signum() < 0) {
            logger.error("wallet have incorrect negative value");
            prepareErrorMessage(errors, "user.wallet.negative", "wallet");
        }
        if (errors.hasErrors()) return;


        if (StringUtils.isEmpty(userForm.getPassword()) || StringUtils.isEmpty(userForm.getRepeatPassword())) {
            logger.error("passwords is empty");
            prepareErrorMessage(errors, "user.password.empty", "repeatPassword");
        } else if (!userForm.getPassword().equals(userForm.getRepeatPassword())) {
            logger.error("passwords mismatch");
            prepareErrorMessage(errors, "user.password.mismatch", "repeatPassword");
        }


        if (!UserStatus.isCorrectValue(userForm.getUserStatus())) {
            logger.error("incorrect user status");
            prepareErrorMessage(errors, "user.status.incorrect", "userStatus");
        }
    }

}
