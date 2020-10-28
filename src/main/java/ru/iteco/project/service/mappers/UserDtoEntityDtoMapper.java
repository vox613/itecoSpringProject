package ru.iteco.project.service.mappers;

import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.UserDto;
import ru.iteco.project.model.User;

import java.util.UUID;

@Service
public class UserDtoEntityDtoMapper implements EntityDtoMapper<User, UserDto> {


    @Override
    public UserDto entityToDto(User entityObject) {
        UserDto userDto = new UserDto();
        if (entityObject != null) {
            userDto.setId(entityObject.getId());
            userDto.setFirstName(entityObject.getFirstName());
            userDto.setSecondName(entityObject.getSecondName());
            userDto.setLastName(entityObject.getLastName());
            userDto.setEmail(entityObject.getEmail());
            userDto.setPhoneNumber(entityObject.getPhoneNumber());
            userDto.setLogin(entityObject.getLogin());
            userDto.setRole(entityObject.getRole());
            userDto.setPassword(entityObject.getPassword());

        }
        return userDto;
    }

    @Override
    public User dtoToEntity(UserDto dtoObject) {
        User user = new User();
        if (dtoObject != null) {
            user.setId(UUID.randomUUID());
            user.setFirstName(dtoObject.getFirstName());
            user.setSecondName(dtoObject.getSecondName());
            user.setLastName(dtoObject.getLastName());
            user.setEmail(dtoObject.getEmail());
            user.setPhoneNumber(dtoObject.getPhoneNumber());
            user.setLogin(dtoObject.getLogin());
            user.setRole(dtoObject.getRole());
            user.setPassword(dtoObject.getPassword());
        }
        return user;
    }
}
