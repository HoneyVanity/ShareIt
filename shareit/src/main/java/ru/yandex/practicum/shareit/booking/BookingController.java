package ru.yandex.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.shareit.booking.dto.BookingDto;
import ru.yandex.practicum.shareit.core.pagination.PaginationMapper;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {
    private final static String USER_ID_HEADER = "X-Sharer-User-Id";
    BookingService bookingService;

    @GetMapping
    public List<Booking> getAllByBooker(
            @RequestHeader(name = USER_ID_HEADER) long bookerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {
        return bookingService.getAllByBooker(bookerId, state, PaginationMapper.toPageable(from, size));
    }

    @GetMapping("/owner")
    public List<Booking> getAllByOwner(
            @RequestHeader(name = USER_ID_HEADER) long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {
        return bookingService.getAllByOwner(ownerId, state, PaginationMapper.toPageable(from, size));
    }

    @GetMapping("/{bookingId}")
    public Booking getById(@PathVariable long bookingId, @RequestHeader(name = USER_ID_HEADER) long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @PostMapping
    public Booking create(@RequestHeader(name = USER_ID_HEADER) long userId, @Valid @RequestBody BookingDto dto) {
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public Booking update(
            @PathVariable long bookingId,
            @RequestHeader(name = USER_ID_HEADER) long ownerId,
            @RequestParam boolean approved
    ) {
        return bookingService.update(bookingId, ownerId, approved);
    }
}
