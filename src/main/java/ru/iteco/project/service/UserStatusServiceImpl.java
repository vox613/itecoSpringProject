package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.controller.dto.UserStatusDtoRequest;
import ru.iteco.project.controller.dto.UserStatusDtoResponse;
import ru.iteco.project.dao.UserRepository;
import ru.iteco.project.dao.UserStatusRepository;
import ru.iteco.project.domain.User;
import ru.iteco.project.domain.UserStatus;
import ru.iteco.project.service.mappers.UserStatusDtoEntityMapper;

import java.util.*;

import static ru.iteco.project.domain.UserRole.UserRoleEnum.ADMIN;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.isEqualsUserRole;


/**
 * Класс реализует функционал сервисного слоя для работы со статусами пользователей
 */
@Service
public class UserStatusServiceImpl implements UserStatusService {

    private static final Logger log = LogManager.getLogger(UserStatusServiceImpl.class.getName());

    /*** Объект доступа к репозиторию статусов пользователей */
    private final UserStatusRepository userStatusRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final UserRepository userRepository;

    /*** Объект сервисного слоя пользователей */
    private final UserService userService;

    /*** Объект маппера dto статуса пользователя в сущность статуса пользователя */
    private final UserStatusDtoEntityMapper userStatusDtoEntityMapper;


    public UserStatusServiceImpl(UserStatusRepository userStatusRepository, UserRepository userRepository,
                                 UserService userService, UserStatusDtoEntityMapper userStatusDtoEntityMapper) {
        this.userStatusRepository = userStatusRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.userStatusDtoEntityMapper = userStatusDtoEntityMapper;
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(readOnly = true)
    public UserStatusDtoResponse getUserStatusById(UUID id) {
        UserStatusDtoResponse userStatusDtoResponse = new UserStatusDtoResponse();
        Optional<UserStatus> optionalUserStatusById = userStatusRepository.findById(id);
        if (optionalUserStatusById.isPresent()) {
            UserStatus userStatus = optionalUserStatusById.get();
            userStatusDtoResponse = userStatusDtoEntityMapper.entityToResponseDto(userStatus);
        }
        return userStatusDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserStatusDtoResponse createUserStatus(UserStatusDtoRequest userStatusDtoRequest) {
        UserStatusDtoResponse userStatusDtoResponse = new UserStatusDtoResponse();
        if (operationIsAllow(userStatusDtoRequest)) {
            UserStatus newUserStatus = userStatusDtoEntityMapper.requestDtoToEntity(userStatusDtoRequest);
            userStatusRepository.save(newUserStatus);
            userStatusDtoResponse = userStatusDtoEntityMapper.entityToResponseDto(newUserStatus);
        }
        return userStatusDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserStatusDtoResponse updateUserStatus(UUID id, UserStatusDtoRequest userStatusDtoRequest) {
        UserStatusDtoResponse userStatusDtoResponse = new UserStatusDtoResponse();
        if (operationIsAllow(userStatusDtoRequest) &&
                Objects.equals(id, userStatusDtoRequest.getId()) &&
                userStatusRepository.existsById(userStatusDtoRequest.getId())) {

            UserStatus userStatus = userStatusDtoEntityMapper.requestDtoToEntity(userStatusDtoRequest);
            userStatus.setId(id);
            userStatusRepository.save(userStatus);
            userStatusDtoResponse = userStatusDtoEntityMapper.entityToResponseDto(userStatus);
        }
        return userStatusDtoResponse;
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public ArrayList<UserStatusDtoResponse> getAllUsersStatuses() {
        ArrayList<UserStatusDtoResponse> userStatusDtoResponseList = new ArrayList<>();
        for (UserStatus userStatus : userStatusRepository.findAll()) {
            userStatusDtoResponseList.add(userStatusDtoEntityMapper.entityToResponseDto(userStatus));
        }
        return userStatusDtoResponseList;
    }


    /**
     * SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     * REQUIRED - в транзакции внешней или новой т.к. используется в других сервисах при удалении записей и
     * должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteUserStatus(UUID id) {
        Optional<UserStatus> userStatusById = userStatusRepository.findById(id);
        if (userStatusById.isPresent()) {
            UserStatus userStatus = userStatusById.get();
            Collection<User> allUsersByStatus = userRepository.findAllByUserStatus(userStatus);
            allUsersByStatus.forEach(user -> userService.deleteUser(user.getId()));
            userStatusRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Метод проверяет разрешена ли для пользователя данная операция
     *
     * @param userStatusDtoRequest - запрос
     * @return true - операция разрешена, false - операция запрещена
     */
    private boolean operationIsAllow(UserStatusDtoRequest userStatusDtoRequest) {
        if ((userStatusDtoRequest != null) && (userStatusDtoRequest.getUserId() != null)) {
            Optional<User> userById = userRepository.findById(userStatusDtoRequest.getUserId());
            if (userById.isPresent()) {
                return isEqualsUserRole(ADMIN, userById.get());
            }
        }
        return false;
    }
}
