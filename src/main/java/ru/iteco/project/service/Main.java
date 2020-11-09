package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger log = LogManager.getLogger(UserServiceImpl.class.getName());

    static User firstUser, secondUser, thirdUser;

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

        thirdUser = new User();
        thirdUser.setId(UUID.randomUUID());
        thirdUser.setFirstName("Иван");
        thirdUser.setSecondName("Иванов");
        thirdUser.setLastName("Иванович");
        thirdUser.setLogin("ivan");
        thirdUser.setPassword("pass");
        thirdUser.setEmail("@mail.com");
        thirdUser.setPhoneNumber("987654p321");
        thirdUser.setRole(Role.ROLE_EXECUTOR);
        thirdUser.setUserStatus(UserStatus.STATUS_ACTIVE);
        thirdUser.setWallet(new BigDecimal("414000"));
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

        thirdTask = new Task();
        thirdTask.setId(UUID.randomUUID());
        thirdTask.setName("");
        thirdTask.setDescription("Описане задания 3");
        thirdTask.setTaskCompletionDate(LocalDateTime.of(2020, 10, 9, 0, 0, 0));
        thirdTask.setPrice(new BigDecimal("-50"));
        thirdTask.setTaskStatus(TaskStatus.TASK_REGISTERED);
        thirdTask.setCustomer(firstUser);
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

        UserService userService = (UserService) context.getBean("userServiceImpl");
        TaskService taskService = (TaskService) context.getBean("taskServiceImpl");
        ContractService contractService = (ContractService) context.getBean("contractServiceImpl");

        userService.addUser(firstUser);
        userService.addUser(secondUser);
        tryAddNullUser(userService);
        tryAddUserWithIncorrectEmailOrPhone(userService, thirdUser);


        taskService.createTask(firstTask);
        taskService.createTask(secondTask);
        tryAddTaskWithoutName(taskService, thirdTask);
        tryAddTaskWithNegativePrice(taskService, thirdTask);

        contractService.createContract(firstContract);
        contractService.createContract(secondContract);
    }

    public static void tryAddNullUser(UserService userService) {
        try {
            userService.addUser(null);
        } catch (RuntimeException e) {
            log.error("Возникла ошибка при добавлении пользователя!", e);
        }
    }

    public static void tryAddUserWithIncorrectEmailOrPhone(UserService userService, User user) {
        try {
            userService.addUser(user);
        } catch (RuntimeException e) {
            log.info("Пользователи с некорректным email или phone не записываются в коллекцию!", e);
        }
    }

    public static void tryAddTaskWithoutName(TaskService taskService, Task task) {
        try {
            taskService.createTask(task);
        } catch (RuntimeException e) {
            log.info("Возникла ошибка при создании задания!", e);
        }
    }

    public static void tryAddTaskWithNegativePrice(TaskService taskService, Task task) {
        try {
            task.setName("Название");
            taskService.createTask(task);
        } catch (RuntimeException e) {
            log.info("Возникла ошибка при создании задания!", e);
        }
    }
}
