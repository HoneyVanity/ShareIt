package ru.yandex.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestJpaRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByUserIdOrderByCreatedDesc(long userId);

    List<Request> findAllByUserIdIsNotOrderByCreatedDesc(long userId, Pageable pageable);
}

