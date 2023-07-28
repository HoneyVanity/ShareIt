package ru.yandex.practicum.shareit.item.dto;

import lombok.Value;

@Value
public class UpdateItemDto {
    String name;
    String description;
    Boolean available;
}