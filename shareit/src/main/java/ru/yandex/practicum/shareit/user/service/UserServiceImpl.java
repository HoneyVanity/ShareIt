package ru.yandex.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.core.exception.DuplicatedEmailException;
import ru.yandex.practicum.shareit.core.exception.FieldValidationException;
import ru.yandex.practicum.shareit.core.exception.NotFoundException;
import ru.yandex.practicum.shareit.user.User;
import ru.yandex.practicum.shareit.user.dao.UserDao;
import ru.yandex.practicum.shareit.user.dto.UserDto;
import ru.yandex.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserDao userDao;
    UserMapper userMapper;

    @Override
    public List<User> getAll() {
        return userDao.findAll();
    }

    @Override
    public User getById(long id) {
        return userDao.getById(id).orElseThrow(() -> new NotFoundException("user", id));
    }

    @Override
    public User create(UserDto dto) {
        Optional.ofNullable(dto.getEmail())
                .orElseThrow(
                        () -> new FieldValidationException("email", "no email"));
        if (!dto.getEmail().isBlank() &&
                checkIfEmailValid(dto.getEmail())) {
            checkIfEmailHasDuplicates(dto.getEmail());
        } else {
            throw new FieldValidationException("email", "invalid email");
        }

        User user = userMapper.toUser(dto);

        return userDao.save(user);
    }

    @Override
    public User update(long id, UserDto dto) {
        User user = userDao.getById(id).orElseThrow(() -> new NotFoundException("user", id));

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            checkIfEmailHasDuplicates(dto.getEmail());
            user.setEmail(dto.getEmail());
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        return userDao.save(user);
    }

    @Override
    public User delete(long id) {
        User user = userDao.getById(id).orElseThrow(() -> new NotFoundException("user", id));
        userDao.deleteById(id);
        return user;
    }

    private void checkIfEmailHasDuplicates(String email) {
        if (userDao.getByEmail(email)) {
            throw new DuplicatedEmailException(email);
        }
    }

    public static boolean checkIfEmailValid(String emailAddress) {

        String regexPattern = "^(.+)@(\\S+)$";
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
