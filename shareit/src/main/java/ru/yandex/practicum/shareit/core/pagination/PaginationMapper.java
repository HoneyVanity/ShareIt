package ru.yandex.practicum.shareit.core.pagination;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.shareit.core.exception.FieldValidationException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationMapper {
    public static Pageable toPageable(Integer from, Integer size) {
        if (from == null || size == null) {
            return null;
        }

        if (size <= 0 || from < 0) {
            throw new FieldValidationException("Pagination", "Incorrect from or size");
        }

        int page = from / size;
        return PageRequest.of(page, size);
    }
}
