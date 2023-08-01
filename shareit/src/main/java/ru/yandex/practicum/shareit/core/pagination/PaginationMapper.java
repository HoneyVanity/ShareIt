package ru.yandex.practicum.shareit.core.pagination;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationMapper {
    public static Pageable toPageable(Integer from, Integer size) {
        if (from == null || size == null) {
            return null;
        }

        int page = from / size;
        return PageRequest.of(page, size);
    }
}
