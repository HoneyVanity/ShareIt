package ru.yandex.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.shareit.booking.dto.ShortBookingDto;
import ru.yandex.practicum.shareit.comment.dto.CommentDto;
import ru.yandex.practicum.shareit.user.User;

import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;

    String name;

    String description;

    Boolean available;

    User owner;

    ShortBookingDto lastBooking;

    ShortBookingDto nextBooking;

    List<CommentDto> comments;

    Long requestId;
}