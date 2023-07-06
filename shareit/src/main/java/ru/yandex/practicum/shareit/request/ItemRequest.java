package ru.yandex.practicum.shareit.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {
    Long id;

    String description;

    LocalDateTime requestor;

    User user;
}