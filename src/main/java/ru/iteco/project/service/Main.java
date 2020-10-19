package ru.iteco.project.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import ru.iteco.project.config.SpringConfig;
import ru.iteco.project.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class Main {

    static User firstUser, secondUser;

    static {
        firstUser = new User();
        firstUser.setId(UUID.randomUUID());
        firstUser.setFirstName("Алексей");
        firstUser.setSecondName("Александров");
        firstUser.setLastName("Игоревич");
        firstUser.setLogin("login");
        firstUser.setPassword("password");
        firstUser.setEmail("123@mail.com");
        firstUser.setPhoneNumber("123456879");
        firstUser.setRole(Role.ROLE_CUSTOMER);
        firstUser.setUserStatus(UserStatus.STATUS_ACTIVE);
        firstUser.setWallet(new BigDecimal("15000"));

        secondUser = new User();
        secondUser.setId(UUID.randomUUID());
        secondUser.setFirstName("Иван");
        secondUser.setSecondName("Иванов");
        secondUser.setLastName("Иванович");
        secondUser.setLogin("ivan");
        secondUser.setPassword("pass");
        secondUser.setEmail("iv@mail.com");
        secondUser.setPhoneNumber("987654321");
        secondUser.setRole(Role.ROLE_EXECUTOR);
        secondUser.setUserStatus(UserStatus.STATUS_ACTIVE);
        secondUser.setWallet(new BigDecimal("414000"));
    }

    static Task firstTask, secondTask, thirdTask;

    static {
        firstTask = new Task();
        firstTask.setId(UUID.randomUUID());
        firstTask.setName("Задание 1");
        firstTask.setDescription("Описане задания 1");
        firstTask.setTaskCompletionDate(LocalDateTime.of(2020, 10, 8, 0, 0, 0));
        firstTask.setPrice(new BigDecimal("3000"));
        firstTask.setTaskStatus(TaskStatus.TASK_REGISTERED);
        firstTask.setCustomer(firstUser);

        secondTask = new Task();
        secondTask.setId(UUID.randomUUID());
        secondTask.setName("Задание 2");
        secondTask.setDescription("Описане задания 2");
        secondTask.setTaskCompletionDate(LocalDateTime.of(2020, 10, 9, 0, 0, 0));
        secondTask.setPrice(new BigDecimal("5000"));
        secondTask.setTaskStatus(TaskStatus.TASK_IN_PROGRESS);
        secondTask.setCustomer(firstUser);
    }

    static Contract firstContract, secondContract;

    static {
        firstContract = new Contract();
        firstContract.setId(UUID.randomUUID());
        firstContract.setExecutor(secondUser);
        firstContract.setTask(firstTask);
        firstContract.setContractStatus(ContractStatus.CONTRACT_REGISTERED);

        secondContract = new Contract();
        secondContract.setId(UUID.randomUUID());
        secondContract.setExecutor(secondUser);
        secondContract.setTask(secondTask);
        secondContract.setContractStatus(ContractStatus.CONTRACT_DONE);
    }


    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);

        UserService userService = (UserService) context.getBean("userService");
        TaskService taskService = (TaskService) context.getBean("taskService");
        ContractService contractService = (ContractService) context.getBean("contractService");

        userService.addUser(firstUser);
        userService.addUser(secondUser);

        taskService.createTask(firstTask);
        taskService.createTask(secondTask);

        contractService.createContract(firstContract);
        contractService.createContract(secondContract);
    }


}
