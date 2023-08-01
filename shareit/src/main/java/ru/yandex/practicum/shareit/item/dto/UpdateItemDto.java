package ru.yandex.practicum.shareit.item.dto;

import lombok.*;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemDto {
    String name;
    String description;
    Boolean available;
}