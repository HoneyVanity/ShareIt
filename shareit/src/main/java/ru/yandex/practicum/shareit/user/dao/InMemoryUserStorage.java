package ru.yandex.practicum.shareit.user.dao;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.shareit.user.User;

import java.util.*;

@Repository
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryUserStorage implements UserDao {
    Long id = 0L;
    Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.of(users.get(id));
    }

    @Override
    public User save(User user) {
        if (!users.containsKey(user.getId())) {
            id = id + 1L;
            user.setId(id);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean getByEmail(String email) {

        return findAll().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }
}
