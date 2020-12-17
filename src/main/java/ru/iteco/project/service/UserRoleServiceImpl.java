package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.controller.dto.UserRoleDtoRequest;
import ru.iteco.project.controller.dto.UserRoleDtoResponse;
import ru.iteco.project.dao.UserDAO;
import ru.iteco.project.dao.UserRoleDAO;
import ru.iteco.project.model.User;
import ru.iteco.project.model.UserRole;
import ru.iteco.project.service.mappers.UserRoleDtoEntityMapper;

import java.util.*;

import static ru.iteco.project.model.UserRole.UserRoleEnum.ADMIN;
import static ru.iteco.project.model.UserRole.UserRoleEnum.isEqualsUserRole;


/**
 * Класс реализует функционал сервисного слоя для работы с ролями пользователей
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    private static final Logger log = LogManager.getLogger(UserRoleServiceImpl.class.getName());


    private final UserRoleDAO userRoleDAO;
    private final UserDAO userDAO;
    private final UserService userService;
    private final UserRoleDtoEntityMapper userRoleDtoEntityMapper;


    public UserRoleServiceImpl(UserRoleDAO userRoleDAO, UserDAO userDAO, UserService userService,
                               UserRoleDtoEntityMapper userRoleDtoEntityMapper) {

        this.userRoleDAO = userRoleDAO;
        this.userDAO = userDAO;
        this.userService = userService;
        this.userRoleDtoEntityMapper = userRoleDtoEntityMapper;
    }


    /**
     *  По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     *  REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(readOnly = true)
    public UserRoleDtoResponse getUserRoleById(UUID id) {
        UserRoleDtoResponse userRoleDtoResponse = new UserRoleDtoResponse();
        Optional<UserRole> optionalUserRoleById = userRoleDAO.findUserRoleById(id);
        if (optionalUserRoleById.isPresent()) {
            UserRole userRole = optionalUserRoleById.get();
            userRoleDtoResponse = userRoleDtoEntityMapper.entityToResponseDto(userRole);
        }
        return userRoleDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserRoleDtoResponse createUserRole(UserRoleDtoRequest userRoleDtoRequest) {
        UserRoleDtoResponse userRoleDtoResponse = new UserRoleDtoResponse();
        if (operationIsAllow(userRoleDtoRequest)) {
            UserRole newUserRole = userRoleDtoEntityMapper.requestDtoToEntity(userRoleDtoRequest);
            userRoleDAO.save(newUserRole);
            userRoleDtoResponse = userRoleDtoEntityMapper.entityToResponseDto(newUserRole);
        }
        return userRoleDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserRoleDtoResponse updateUserRole(UUID id, UserRoleDtoRequest userRoleDtoRequest) {
        UserRoleDtoResponse userRoleDtoResponse = new UserRoleDtoResponse();
        if (operationIsAllow(userRoleDtoRequest) &&
                Objects.equals(id, userRoleDtoRequest.getId()) &&
                userRoleDAO.userRoleWithIdIsExist(userRoleDtoRequest.getId())) {

            UserRole userRole = userRoleDtoEntityMapper.requestDtoToEntity(userRoleDtoRequest);
            userRole.setId(id);
            userRoleDAO.update(userRole);
            userRoleDtoResponse = userRoleDtoEntityMapper.entityToResponseDto(userRole);
        }
        return userRoleDtoResponse;
    }


    /**
     *  По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public ArrayList<UserRoleDtoResponse> getAllUsersRoles() {
        ArrayList<UserRoleDtoResponse> userRoleDtoResponseList = new ArrayList<>();
        for (UserRole userRole : userRoleDAO.getAll()) {
            userRoleDtoResponseList.add(userRoleDtoEntityMapper.entityToResponseDto(userRole));
        }
        return userRoleDtoResponseList;
    }


    /**
     *  SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     *  REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     *  должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteUserRole(UUID id) {
        Optional<UserRole> userRoleById = userRoleDAO.findUserRoleById(id);
        if (userRoleById.isPresent()) {
            UserRole userRole = userRoleById.get();
            List<User> allUsersByRole = userDAO.getAllUsersByRole(userRole);
            allUsersByRole.forEach(user -> userService.deleteUser(user.getId()));
            userRoleDAO.deleteByPK(id);
            return true;
        }
        return false;
    }


    /**
     * Метод проверяет разрешена ли для пользователя данная операция
     *
     * @param userRoleDtoRequest - запрос
     * @return true - операция разрешена, false - операция запрещена
     */
    private boolean operationIsAllow(UserRoleDtoRequest userRoleDtoRequest) {
        if ((userRoleDtoRequest != null) && (userRoleDtoRequest.getUserId() != null)) {
            Optional<User> userById = userDAO.findUserById(userRoleDtoRequest.getUserId());
            if (userById.isPresent()) {
                UserRole role = userById.get().getRole();
                return (role != null) && isEqualsUserRole(ADMIN, role.getValue());
            }
        }
        return false;
    }
}
