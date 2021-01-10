package ru.iteco.project.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteco.project.resource.UserStatusResource;
import ru.iteco.project.resource.dto.UserStatusBaseDto;
import ru.iteco.project.resource.dto.UserStatusDtoRequest;
import ru.iteco.project.resource.dto.UserStatusDtoResponse;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.UserStatusSearchDto;
import ru.iteco.project.service.UserStatusService;
import ru.iteco.project.validator.UserStatusDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с UserStatus
 */
@RestController
public class UserStatusController implements UserStatusResource {

    /*** Объект сервисного слоя для UserRole*/
    private final UserStatusService userStatusService;

    /*** Объект валидатора для UserDtoRequest*/
    private final UserStatusDtoRequestValidator userStatusDtoRequestValidator;


    public UserStatusController(UserStatusService userStatusService, UserStatusDtoRequestValidator userStatusDtoRequestValidator) {
        this.userStatusService = userStatusService;
        this.userStatusDtoRequestValidator = userStatusDtoRequestValidator;
    }

    @Override
    public ResponseEntity<List<UserStatusDtoResponse>> getAllUserStatus() {
        List<UserStatusDtoResponse> allUsersStatuses = userStatusService.getAllUsersStatuses();
        return ResponseEntity.ok().body(allUsersStatuses);
    }


    @Override
    public ResponseEntity<UserStatusDtoResponse> getUserStatus(@PathVariable UUID id) {
        UserStatusDtoResponse userStatusById = userStatusService.getUserStatusById(id);
        if ((userStatusById != null) && (userStatusById.getId() != null)) {
            return ResponseEntity.ok().body(userStatusById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public PageDto getUsers(@RequestBody(required = false) UserStatusSearchDto userStatusSearchDto,
                            @PageableDefault(size = 5,
                                    page = 0,
                                    sort = {"createdAt"},
                                    direction = Sort.Direction.ASC) Pageable pageable) {

        return userStatusService.getStatus(userStatusSearchDto, pageable);
    }


    @Override
    public ResponseEntity<? extends UserStatusBaseDto> createUserStatus(@Validated @RequestBody UserStatusDtoRequest userStatusDtoRequest,
                                                                        BindingResult result,
                                                                        UriComponentsBuilder componentsBuilder) {
        if (result.hasErrors()) {
            userStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userStatusDtoRequest);
        }

        UserStatusDtoResponse userStatusDtoResponse = userStatusService.createUserStatus(userStatusDtoRequest);

        if (userStatusDtoResponse.getId() != null) {
            URI uri = componentsBuilder.path("/statuses/users/" + userStatusDtoResponse.getId()).buildAndExpand(userStatusDtoResponse).toUri();
            return ResponseEntity.created(uri).body(userStatusDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }
    }


    @Override
    public ResponseEntity<? extends UserStatusBaseDto> updateUserStatus(@PathVariable UUID id,
                                                                        @Validated @RequestBody UserStatusDtoRequest userStatusDtoRequest,
                                                                        BindingResult result) {

        if (result.hasErrors()) {
            userStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userStatusDtoRequest);
        }

        UserStatusDtoResponse statusDtoResponse = userStatusService.updateUserStatus(id, userStatusDtoRequest);

        if (statusDtoResponse != null) {
            return ResponseEntity.ok().body(statusDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(null);
        }
    }


    @Override
    public ResponseEntity<Object> deleteUserStatus(@PathVariable UUID id) {
        if (userStatusService.deleteUserStatus(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @InitBinder(value = "userStatusDtoRequest")
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(userStatusDtoRequestValidator);
    }

}
