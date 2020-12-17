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
import ru.iteco.project.controller.dto.UserStatusDtoRequest;
import ru.iteco.project.controller.dto.UserStatusDtoResponse;
import ru.iteco.project.controller.searching.PageDto;
import ru.iteco.project.controller.searching.SearchDto;
import ru.iteco.project.controller.searching.UserStatusSearchDto;
import ru.iteco.project.dao.UserRepository;
import ru.iteco.project.dao.UserStatusRepository;
import ru.iteco.project.domain.User;
import ru.iteco.project.domain.UserStatus;
import ru.iteco.project.service.mappers.UserStatusDtoEntityMapper;
import ru.iteco.project.service.specifications.SpecificationSupport;

import javax.persistence.criteria.Predicate;
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
            UserStatus save = userStatusRepository.save(newUserStatus);
            userStatusDtoResponse = userStatusDtoEntityMapper.entityToResponseDto(save);
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
            UserStatus save = userStatusRepository.save(userStatus);
            userStatusDtoResponse = userStatusDtoEntityMapper.entityToResponseDto(save);
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


    public PageDto<UserStatusDtoResponse> getStatus(SearchDto<UserStatusSearchDto> searchDto, Pageable pageable) {
        Page<UserStatus> page;
        if ((searchDto != null) && (searchDto.searchData() != null)) {
            page = userStatusRepository.findAll(getSpec(searchDto), pageable);
        } else {
            page = userStatusRepository.findAll(pageable);
        }

        List<UserStatusDtoResponse> userStatusDtoResponses = page.map(userStatusDtoEntityMapper::entityToResponseDto).toList();
        return new PageDto<>(userStatusDtoResponses, page.getTotalElements(), page.getTotalPages());

    }

    /**
     * Метод получения спецификации для поиска
     *
     * @param searchDto - объект dto с данными для поиска
     * @return - объект спецификации для поиска данных
     */
    private Specification<UserStatus> getSpec(SearchDto<UserStatusSearchDto> searchDto) {
        SpecificationSupport<UserStatus> specSupport = new SpecificationSupport<>();

        return (root, query, builder) -> {
            UserStatusSearchDto userStatusSearchDto = searchDto.searchData();
            ArrayList<Predicate> predicates = new ArrayList<>();

            if (!ObjectUtils.isEmpty(userStatusSearchDto.getValue())) {
                predicates.add(specSupport.getEqualsPredicate(builder, specSupport.getPath(root, "value"),
                        userStatusSearchDto.getValue()));
            }

            if (!ObjectUtils.isEmpty(userStatusSearchDto.getDescription())) {
                predicates.add(specSupport.getLikePredicate(builder, specSupport.getPath(root, "description"),
                        userStatusSearchDto.getDescription()));
            }
            return builder.or(predicates.toArray(new Predicate[]{}));
        };
    }

}


