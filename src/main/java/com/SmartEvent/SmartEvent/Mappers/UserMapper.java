package com.SmartEvent.SmartEvent.Mappers;

import com.SmartEvent.SmartEvent.Dto.UserDto;
import com.SmartEvent.SmartEvent.Model.User;

public class UserMapper {

    // Convert User → UserDto
    public static UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setUserName(user.getUsername());
        dto.setPhoto(user.getPhoto());
        dto.setRole(user.getRole());
        return dto;
    }

    // Convert UserDto → User
    public static User toEntity(UserDto dto) {
        User user = new User();
        user.setId(user.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setUserName(dto.getUserName());
        user.setPhoto(dto.getPhoto());
        user.setRole(dto.getRole());
        return user;
    }
}
