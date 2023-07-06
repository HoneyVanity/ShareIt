package ru.yandex.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.shareit.user.User;
@Component
public class UserMapper {
    public User toUser(UserDto dto) {
        return new User(null,
                dto.getName(),
                dto.getEmail()
        );
    }

    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getName(),
                user.getEmail()
        );
    }
}
