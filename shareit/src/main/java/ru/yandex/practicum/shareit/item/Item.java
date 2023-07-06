package ru.yandex.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.shareit.request.ItemRequest;
import ru.yandex.practicum.shareit.user.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {

    Long id;

    String name;

    String description;

    boolean available;

    User owner;

    ItemRequest request;
}