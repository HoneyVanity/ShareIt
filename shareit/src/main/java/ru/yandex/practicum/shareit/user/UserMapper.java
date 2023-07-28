package ru.yandex.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.yandex.practicum.shareit.user.dto.CreateUserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(CreateUserDto dto);
}
