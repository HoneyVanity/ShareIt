package ru.yandex.practicum.shareit.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemId(long itemId);

    @Query("select c " +
            "from Comment c " +
            "inner join c.item i " +
            "inner join i.owner o " +
            "where o.id = :userId "
    )
    List<Comment> findAllByItem_Owner_Id(long userId);
}
