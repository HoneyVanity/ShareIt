package ru.yandex.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query("select i " +
            "from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "and i.available is true")
    List<Item> findAllByText(String text, Pageable pageable);

    List<Item> findAllByRequestId(long requestId);

    @Query("select i " +
            "from Item i " +
            "where i.request.id is not null "
    )
    List<Item> findAllWithRequestId();
}
