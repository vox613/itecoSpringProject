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
import ru.iteco.project.controller.dto.UserRoleDtoRequest;
import ru.iteco.project.controller.dto.UserRoleDtoResponse;
import ru.iteco.project.controller.searching.PageDto;
import ru.iteco.project.controller.searching.SearchDto;
import ru.iteco.project.controller.searching.UserRoleSearchDto;
import ru.iteco.project.dao.UserRepository;
import ru.iteco.project.dao.UserRoleRepository;
import ru.iteco.project.domain.User;
import ru.iteco.project.domain.UserRole;
import ru.iteco.project.service.mappers.UserRoleDtoEntityMapper;
import ru.iteco.project.service.specifications.SpecificationSupport;

import java.util.*;

import static ru.iteco.project.domain.UserRole.UserRoleEnum.ADMIN;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.isEqualsUserRole;


/**
 * Класс реализует функционал сервисного слоя для работы с ролями пользователей
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    private static final Logger log = LogManager.getLogger(UserRoleServiceImpl.class.getName());

    /*** Объект доступа к репозиторию ролей пользователей */
    private final UserRoleRepository userRoleRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final UserRepository userRepository;

    /*** Объект сервисного слоя пользователей */
    private final UserService userService;

    /*** Объект маппера dto роли пользователя в сущность роли пользователя */
    private final UserRoleDtoEntityMapper userRoleDtoEntityMapper;


    public UserRoleServiceImpl(UserRoleRepository userRoleRepository, UserRepository userRepository, UserService userService,
                               UserRoleDtoEntityMapper userRoleDtoEntityMapper) {

        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.userRoleDtoEntityMapper = userRoleDtoEntityMapper;
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(readOnly = true)
    public UserRoleDtoResponse getUserRoleById(UUID id) {
        UserRoleDtoResponse userRoleDtoResponse = new UserRoleDtoResponse();
        Optional<UserRole> optionalUserRoleById = userRoleRepository.findById(id);
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
            UserRole save = userRoleRepository.save(newUserRole);
            userRoleDtoResponse = userRoleDtoEntityMapper.entityToResponseDto(save);
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
                userRoleRepository.existsById(userRoleDtoRequest.getId())) {

            UserRole userRole = userRoleDtoEntityMapper.requestDtoToEntity(userRoleDtoRequest);
            userRole.setId(id);
            UserRole save = userRoleRepository.save(userRole);
            userRoleDtoResponse = userRoleDtoEntityMapper.entityToResponseDto(save);
        }
        return userRoleDtoResponse;
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public ArrayList<UserRoleDtoResponse> getAllUsersRoles() {
        ArrayList<UserRoleDtoResponse> userRoleDtoResponseList = new ArrayList<>();
        for (UserRole userRole : userRoleRepository.findAll()) {
            userRoleDtoResponseList.add(userRoleDtoEntityMapper.entityToResponseDto(userRole));
        }
        return userRoleDtoResponseList;
    }


    /**
     * SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     * REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     * должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteUserRole(UUID id) {
        Optional<UserRole> userRoleById = userRoleRepository.findById(id);
        if (userRoleById.isPresent()) {
            UserRole userRole = userRoleById.get();
            Collection<User> allUsersByRole = userRepository.findAllByRole(userRole);
            allUsersByRole.forEach(user -> userService.deleteUser(user.getId()));
            userRoleRepository.deleteById(id);
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
            Optional<User> userById = userRepository.findById(userRoleDtoRequest.getUserId());
            if (userById.isPresent()) {
                UserRole role = userById.get().getRole();
                return (role != null) && isEqualsUserRole(ADMIN, role.getValue());
            }
        }
        return false;
    }

    public PageDto<UserRoleDtoResponse> getRoles(SearchDto<UserRoleSearchDto> searchDto, Pageable pageable) {
        Page<UserRole> page;
        if ((searchDto != null) && (searchDto.searchData() != null)) {
            page = userRoleRepository.findAll(getSpec(searchDto), pageable);
        } else {
            page = userRoleRepository.findAll(pageable);
        }

        List<UserRoleDtoResponse> userRoleDtoResponses = page.map(userRoleDtoEntityMapper::entityToResponseDto).toList();
        return new PageDto<>(userRoleDtoResponses, page.getTotalElements(), page.getTotalPages());

    }

    /**
     * Метод получения спецификации для поиска
     *
     * @param searchDto - объект dto с данными для поиска
     * @return - объект спецификации для поиска данных
     */
    private Specification<UserRole> getSpec(SearchDto<UserRoleSearchDto> searchDto) {
        SpecificationSupport<UserRole> specSupport = new SpecificationSupport<>();
        return (root, query, builder) -> {

            UserRoleSearchDto userRoleSearchDto = searchDto.searchData();

            if (!ObjectUtils.isEmpty(userRoleSearchDto.getValue())) {
                return builder.equal(specSupport.getPath(root, "value"), userRoleSearchDto.getValue());
            }
            return builder.and();
        };
    }

}
