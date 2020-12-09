package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.dao.TaskRepository;
import ru.iteco.project.dao.UserRepository;
import ru.iteco.project.domain.User;
import ru.iteco.project.service.mappers.UserDtoEntityMapper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.iteco.project.domain.UserStatus.UserStatusEnum.CREATED;
import static ru.iteco.project.domain.UserStatus.UserStatusEnum.isEqualsUserStatus;

/**
 * Класс реализует функционал сервисного слоя для работы с пользователями
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LogManager.getLogger(UserServiceImpl.class.getName());

    /*** Объект доступа к репозиторию пользователей */
    private final UserRepository userRepository;

    /*** Объект доступа к репозиторию заданий */
    private final TaskRepository taskRepository;

    /*** Объект сервисного слоя заданий */
    private final TaskService taskService;

    /*** Объект маппера dto пользователя в сущность пользователя */
    private final UserDtoEntityMapper userMapper;


    public UserServiceImpl(UserRepository userRepository, TaskRepository taskRepository, TaskService taskService, UserDtoEntityMapper userMapper) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.taskService = taskService;
        this.userMapper = userMapper;
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(readOnly = true)
    public UserDtoResponse getUserById(UUID uuid) {
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        Optional<User> optionalUser = userRepository.findById(uuid);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            userDtoResponse = userMapper.entityToResponseDto(user);
        }
        return userDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserDtoResponse createUser(UserDtoRequest userDtoRequest) {
        UserDtoResponse userDtoResponse = null;
        if (isCorrectLoginEmail(userDtoRequest.getLogin(), userDtoRequest.getEmail())
                && isEqualsUserStatus(CREATED, userDtoRequest.getUserStatus())) {
            User newUser = userMapper.requestDtoToEntity(userDtoRequest);
            userRepository.save(newUser);
            userDtoResponse = userMapper.entityToResponseDto(newUser);
        }
        return userDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<UserDtoResponse> createBundleUsers(List<UserDtoRequest> userDtoRequestList) {
        List<User> usersList = userDtoRequestList.stream().map(userMapper::requestDtoToEntity).collect(Collectors.toList());
        userRepository.saveAll(usersList);
        return usersList.stream().map(userMapper::entityToResponseDto).collect(Collectors.toList());
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserDtoResponse updateUser(UUID id, UserDtoRequest userDtoRequest) {
        UserDtoResponse userDtoResponse = null;
        if (userRepository.existsById(id) && Objects.equals(id, userDtoRequest.getId())) {
            User user = userMapper.requestDtoToEntity(userDtoRequest);
            user.setId(id);
            userRepository.save(user);
            userDtoResponse = userMapper.entityToResponseDto(user);
        }
        return userDtoResponse;
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public ArrayList<UserDtoResponse> getAllUsers() {
        ArrayList<UserDtoResponse> userDtoResponseList = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            userDtoResponseList.add(userMapper.entityToResponseDto(user));
        }
        return userDtoResponseList;
    }

    /**
     * SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     * REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     * должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteUser(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            taskRepository.findTasksByUser(user)
                    .forEach(task -> taskService.deleteTask(task.getId()));
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }


    public UserDtoEntityMapper getUserMapper() {
        return userMapper;
    }

    /**
     * Метод проверяет что пользователя с таким логином и email не существует
     *
     * @param login - логин пользователя
     * @param email - email пользователя
     * @return - true - логин и email не пусты и пользователя с такими данными не существует, false - в любом ином случае
     */
    private boolean isCorrectLoginEmail(String login, String email) {
        return !userRepository.existsByEmailOrLogin(email, login);
    }
}
