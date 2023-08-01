package ru.yandex.practicum.shareit.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.shareit.item.Item;
import ru.yandex.practicum.shareit.item.dto.CreateItemDto;
import ru.yandex.practicum.shareit.request.Request;
import ru.yandex.practicum.shareit.request.dto.RequestDto;
import ru.yandex.practicum.shareit.user.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {
    public static User makeUser(long id) {
        return User.builder()
                .id(id)
                .name("Test name")
                .email("test@test.test").build();
    }

    public static Item makeItem(long id, boolean available, User user) {
        return Item.builder()
                .id(id)
                .name("Test name")
                .description("Test description")
                .available(available)
                .owner(user)
                .build();
    }

    public static CreateItemDto makeCreateItemDto(boolean available, Long requestId) {
        return CreateItemDto.builder()
                .name("Test name")
                .description("Test description")
                .available(available)
                .requestId(requestId)
                .build();
    }

    public static Request makeRequest(long id, LocalDateTime created, User user) {
        return Request.builder()
                .id(id)
                .description("Test description")
                .created(created)
                .user(user)
                .build();
    }

    public static RequestDto makeRequestDto(long id) {
        return RequestDto.builder()
                .id(id)
                .description("Test description")
                .created(LocalDateTime.now())
                .build();
    }
}