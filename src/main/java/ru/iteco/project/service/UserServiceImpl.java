package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.controller.searching.PageDto;
import ru.iteco.project.controller.searching.SearchDto;
import ru.iteco.project.controller.searching.UserSearchDto;
import ru.iteco.project.dao.TaskRepository;
import ru.iteco.project.dao.UserRepository;
import ru.iteco.project.domain.User;
import ru.iteco.project.domain.UserRole;
import ru.iteco.project.domain.UserStatus;
import ru.iteco.project.exception.InvalidUserRoleException;
import ru.iteco.project.exception.InvalidUserStatusException;
import ru.iteco.project.service.mappers.UserDtoEntityMapper;
import ru.iteco.project.service.specifications.SpecificationSupport;

import javax.persistence.criteria.Predicate;
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
            User save = userRepository.save(newUser);
            userDtoResponse = userMapper.entityToResponseDto(save);
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
        List<User> users = userRepository.saveAll(usersList);
        return users.stream().map(userMapper::entityToResponseDto).collect(Collectors.toList());
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserDtoResponse updateUser(UserDtoRequest userDtoRequest) {
        UserDtoResponse userDtoResponse = null;
        if (userRepository.existsById(userDtoRequest.getId())) {
            User user = userMapper.requestDtoToEntity(userDtoRequest);
            user.setId(userDtoRequest.getId());
            User save = userRepository.save(user);
            userDtoResponse = userMapper.entityToResponseDto(save);
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


    public PageDto<UserDtoResponse> getUsers(SearchDto<UserSearchDto> searchDto, Pageable pageable) {
        Page<User> page;
        if ((searchDto != null) && (searchDto.searchData() != null)) {
            page = userRepository.findAll(getSpec(searchDto), pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        List<UserDtoResponse> userDtoResponses = page.map(userMapper::entityToResponseDto).toList();
        return new PageDto<>(userDtoResponses, page.getTotalElements(), page.getTotalPages());

    }


    /**
     * Метод получения спецификации для поиска
     *
     * @param searchDto - объект dto с данными для поиска
     * @return - объект спецификации для поиска данных
     */
    private Specification<User> getSpec(SearchDto<UserSearchDto> searchDto) {
        SpecificationSupport<User> specSupport = new SpecificationSupport<>();
        return (root, query, builder) -> {

            UserSearchDto userSearchDto = searchDto.searchData();
            ArrayList<Predicate> predicates = new ArrayList<>();

            if (!ObjectUtils.isEmpty(userSearchDto.getRole())) {
                UserRole userRole = userMapper.getUserRoleRepository()
                        .findUserRoleByValue(userSearchDto.getRole())
                        .orElseThrow(InvalidUserRoleException::new);

                predicates.add(specSupport.getEqualsPredicate(builder, specSupport.getPath(root, "role"), userRole));
            }

            if (!ObjectUtils.isEmpty(userSearchDto.getUserStatus())) {
                UserStatus userStatus = userMapper.getUserStatusRepository()
                        .findUserStatusByValue(userSearchDto.getUserStatus())
                        .orElseThrow(InvalidUserStatusException::new);

                predicates.add(specSupport.getEqualsPredicate(builder, specSupport.getPath(root, "userStatus"), userStatus));
            }
            return builder.and(predicates.toArray(new Predicate[]{}));
        };
    }

}
