package ru.yandex.practicum.shareit.request.dto;

import lombok.*;
import ru.yandex.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class RequestDto {
    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}
