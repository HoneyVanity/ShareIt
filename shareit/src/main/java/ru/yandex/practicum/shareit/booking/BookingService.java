package ru.yandex.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.booking.dto.BookingDto;
import ru.yandex.practicum.shareit.core.exception.FieldValidationException;
import ru.yandex.practicum.shareit.core.exception.NotFoundException;
import ru.yandex.practicum.shareit.core.exception.UnsupportedStatusException;
import ru.yandex.practicum.shareit.item.Item;
import ru.yandex.practicum.shareit.item.service.ItemService;
import ru.yandex.practicum.shareit.user.User;
import ru.yandex.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {
    BookingRepository bookingRepository;
    BookingMapper bookingMapper;
    UserService userService;
    ItemService itemService;

    public List<Booking> getAllByBooker(long bookerId, String state) {
        userService.getById(bookerId);

        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
            default:
                throw new UnsupportedStatusException();
        }
    }

    public List<Booking> getAllByOwner(long ownerId, String state) {
        userService.getById(ownerId);

        switch (state) {
            case "ALL":
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
            case "CURRENT":
                return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
            default:
                throw new UnsupportedStatusException();
        }
    }

    public Booking getById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("booking", bookingId));

        boolean isOwner = booking.getItem().getOwner().getId() == userId;
        boolean isBooker = booking.getBooker().getId() == userId;

        if (!(isOwner || isBooker)) {
            throw new NotFoundException("booking", bookingId);
        }

        return booking;
    }

    public Booking create(long userId, BookingDto dto) {

        User booker = userService.getById(userId);
        Item item = itemService.getById(dto.getItemId(), userId);

        boolean isItemUnavailable = !item.getAvailable();

        if (isItemUnavailable) {
            throw new FieldValidationException("itemId", "Item with this id is unavailable");
        }

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("booking", item.getId());
        }

        Booking booking = bookingMapper.ToBooking(dto);

        boolean isStartInPast = booking.getStart().isBefore(LocalDateTime.now());
        boolean isEndInPast = booking.getEnd().isBefore(LocalDateTime.now());
        boolean isEndBeforeStart = booking.getEnd().isBefore(booking.getStart());
        boolean isEndEqualsStart = booking.getEnd().isEqual(booking.getStart());

      List<Booking> bookings = itemService.getAllBookings(item.getId());
        if (!bookings.isEmpty()) {
            boolean isNotIntersection = bookings.stream()
                    .allMatch(b -> (booking.getStart().isBefore(b.getStart()) &&
                            booking.getEnd().isBefore(b.getStart())) ||
                            (booking.getStart().isAfter(b.getEnd()) &&
                                    booking.getEnd().isAfter(b.getEnd())));
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

        return bookingRepository.save(booking);
    }

    public Booking update(long bookingId, long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("booking", bookingId));

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

        return bookingRepository.save(booking);
    }
}
