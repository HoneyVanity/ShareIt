package ru.yandex.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.booking.Booking;
import ru.yandex.practicum.shareit.booking.BookingMapper;
import ru.yandex.practicum.shareit.booking.BookingRepository;
import ru.yandex.practicum.shareit.booking.BookingStatus;
import ru.yandex.practicum.shareit.comment.Comment;
import ru.yandex.practicum.shareit.comment.CommentMapper;
import ru.yandex.practicum.shareit.comment.CommentRepository;
import ru.yandex.practicum.shareit.comment.dto.CommentDto;
import ru.yandex.practicum.shareit.comment.dto.CreateCommentDto;
import ru.yandex.practicum.shareit.core.exception.FieldValidationException;
import ru.yandex.practicum.shareit.core.exception.NotFoundException;
import ru.yandex.practicum.shareit.item.Item;
import ru.yandex.practicum.shareit.item.ItemJpaRepository;
import ru.yandex.practicum.shareit.item.ItemMapper;
import ru.yandex.practicum.shareit.item.dto.CreateItemDto;
import ru.yandex.practicum.shareit.item.dto.UpdateItemDto;
import ru.yandex.practicum.shareit.user.User;
import ru.yandex.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemService {

    ItemJpaRepository repo;
    BookingRepository bookingRepo;
    UserService userService;
    ItemMapper mapper;
    BookingMapper bookingMapper;
    CommentMapper commentMapper;
    CommentRepository commentRepo;

    public List<Item> getByUserId(Long userId) {
        return repo.findAllByOwnerId(userId)
                .stream()
                .peek(item -> {
                    List<Booking> bookings = bookingRepo.findAllByItemIdOrderByStartAsc(item.getId());
                    item.setNextBooking(bookingMapper.toShortBookingDto(getNextBooking(bookings)));
                    item.setLastBooking(bookingMapper.toShortBookingDto(getLastBooking(bookings)));
                    item.setComments(
                            commentRepo.findAllByItemId(item.getId())
                                    .stream()
                                    .map(commentMapper::toCommentDto)
                                    .collect(Collectors.toList())
                    );

                })
                .collect(Collectors.toList());
    }

    public List<Item> searchByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return repo.findAllByText(text);

    }

    public Item getById(long id, Long userId) {

        Item item = repo.findById(id).orElseThrow(() -> new NotFoundException("item", id));

        if (Objects.equals(item.getOwner().getId(), userId)) {
            List<Booking> bookings = bookingRepo.findAllByItemIdOrderByStartAsc(id);

            item.setNextBooking(bookingMapper.toShortBookingDto(getNextBooking(bookings)));
            item.setLastBooking(bookingMapper.toShortBookingDto(getLastBooking(bookings)));
        }

        List<CommentDto> comments = commentRepo.findAllByItemId(item.getId())
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
        item.setComments(comments);

        return item;
    }

    public Item create(Long userId, CreateItemDto dto) {

        User user = userService.getById(userId);
        Item newItem = mapper.toItem(dto);
        newItem.setOwner(user);

        return repo.save(newItem);
    }

    public Item update(long id, Long userId, UpdateItemDto dto) {
        User user = userService.getById(userId);
        Item item = repo.findById(id).orElseThrow(() -> new NotFoundException("item", id));

        if (!user.equals(item.getOwner())) {
            throw new NotFoundException("item", id);
        }

        if (dto.getName() != null) {
            item.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        return repo.save(item);
    }

    public Item delete(long id) {
        Item item = repo.findById(id).orElseThrow(() -> new NotFoundException("item", id));
        repo.deleteById(id);
        return item;
    }

    private Booking getNextBooking(List<Booking> bookings) {
        List<Booking> filteredBookings = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()) && !booking.getStatus().equals(BookingStatus.REJECTED))
                .collect(Collectors.toList());

        return filteredBookings.isEmpty() ? null : filteredBookings.get(0);
    }

    private Booking getLastBooking(List<Booking> bookings) {
        List<Booking> filteredBookings = bookings.stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()) ||
                        (booking.getStart().isBefore(LocalDateTime.now()) &&
                                booking.getEnd().isAfter(LocalDateTime.now())))
                .collect(Collectors.toList());

        return filteredBookings.isEmpty() ? null : filteredBookings.get(filteredBookings.size() - 1);
    }

    public CommentDto comment(long id, long userId, CreateCommentDto commentDto) {
        Item item = repo.findById(id).orElseThrow(() -> new NotFoundException("item", id));
        User user = userService.getById(userId);

        List<Booking> bookings = bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new FieldValidationException("userId", "User didn't book this item");
        }

        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        return commentMapper.toCommentDto(commentRepo.save(comment));
    }
}
