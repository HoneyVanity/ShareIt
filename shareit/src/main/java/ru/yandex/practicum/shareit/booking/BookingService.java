package ru.yandex.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.booking.dto.BookingDto;
import ru.yandex.practicum.shareit.core.exception.FieldValidationException;
import ru.yandex.practicum.shareit.core.exception.NotFoundException;
import ru.yandex.practicum.shareit.core.exception.UnsupportedStatusException;
import ru.yandex.practicum.shareit.item.Item;
import ru.yandex.practicum.shareit.item.ItemJpaRepository;
import ru.yandex.practicum.shareit.item.service.ItemService;
import ru.yandex.practicum.shareit.user.User;
import ru.yandex.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {
    BookingJpaRepository repo;
    BookingMapper mapper;
    UserService userService;
    ItemJpaRepository itemRepo;
    ItemService itemService;

    public List<Booking> getAllByBooker(long bookerId, String state, Pageable pageable) {
        userService.getById(bookerId);

        switch (state) {
            case "ALL":
                return repo.findAllByBookerIdOrderByStartDesc(bookerId, pageable);
            case "CURRENT":
                return repo.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
            case "PAST":
                return repo.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now(), pageable);
            case "FUTURE":
                return repo.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now(), pageable);
            case "WAITING":
                return repo.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING, pageable);
            case "REJECTED":
                return repo.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED, pageable);
            default:
                throw new UnsupportedStatusException();
        }
    }

    public List<Booking> getAllByOwner(long ownerId, String state, Pageable pageable) {
        userService.getById(ownerId);

        switch (state) {
            case "ALL":
                return repo.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageable);
            case "CURRENT":
                return repo.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
            case "PAST":
                return repo.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), pageable);
            case "FUTURE":
                return repo.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now(), pageable);
            case "WAITING":
                return repo.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING, pageable);
            case "REJECTED":
                return repo.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED, pageable);
            default:
                throw new UnsupportedStatusException();
        }
    }

    public Booking getById(long bookingId, long userId) {
        Booking booking = repo.findById(bookingId).orElseThrow(() -> new NotFoundException("booking", bookingId));

        boolean isOwner = booking.getItem().getOwner().getId() == userId;
        boolean isBooker = booking.getBooker().getId() == userId;

        if (!(isOwner || isBooker)) {
            throw new NotFoundException("booking", bookingId);
        }

        return booking;
    }

    public Booking create(long userId, BookingDto dto) {

        User booker = userService.getById(userId);
        Item item = itemRepo.findById(dto.getItemId()).orElseThrow(() -> new NotFoundException("item", dto.getItemId()));

        boolean isItemUnavailable = !item.getAvailable();

        if (isItemUnavailable) {
            throw new FieldValidationException("itemId", "Item with this id is unavailable");
        }

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("booking", item.getId());
        }

        Booking booking = mapper.ToBooking(dto);

        boolean isStartInPast = booking.getStart().isBefore(LocalDateTime.now());
        boolean isEndInPast = booking.getEnd().isBefore(LocalDateTime.now());
        boolean isEndBeforeStart = booking.getEnd().isBefore(booking.getStart());
        boolean isEndEqualsStart = booking.getEnd().isEqual(booking.getStart());

        List<Booking> bookings = itemService.getAllBookings(item.getId());
        if (!bookings.isEmpty()) {
            boolean isNotIntersection = bookings.stream()
                    .allMatch(b -> booking.getEnd().isBefore(b.getStart()) || booking.getStart().isAfter(b.getEnd()));

            if (!isNotIntersection) {
                throw new FieldValidationException("start | end", "Item already booked on these dates");
            }
        }

        if (isStartInPast ||
                isEndInPast ||
                isEndBeforeStart ||
                isEndEqualsStart) {
            throw new FieldValidationException("start | end", "Time is incorrect");
        }

        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        return repo.save(booking);
    }

    public Booking update(long bookingId, long ownerId, boolean approved) {
        Booking booking = repo.findById(bookingId).orElseThrow(() -> new NotFoundException("booking", bookingId));

        boolean isOwner = booking.getItem().getOwner().getId() == ownerId;

        if (!isOwner) {
            throw new NotFoundException("booking", bookingId);
        }

        boolean isBookingApprovedOrRejected = booking.getStatus() == BookingStatus.APPROVED || booking.getStatus() == BookingStatus.REJECTED;

        if (isBookingApprovedOrRejected) {
            throw new FieldValidationException("bookingId", "Booking is already approved or rejected");
        }

        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);

        return repo.save(booking);
    }
}
