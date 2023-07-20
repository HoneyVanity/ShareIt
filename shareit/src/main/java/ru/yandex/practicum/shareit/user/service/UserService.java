package ru.yandex.practicum.shareit.user.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.shareit.user.User;
import ru.yandex.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Component
public interface UserService {

    List<User> getAll();

    User getById(long id);

    User create(UserDto dto);

    User update(long id, UserDto dto);

    User delete(long id);

}
