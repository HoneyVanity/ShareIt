package ru.yandex.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserDao {
    List<User> findAll();

    Optional<User> getById(Long id);

    User save(User user);

    boolean getByEmail(String email);

    void deleteById(Long id);
}
