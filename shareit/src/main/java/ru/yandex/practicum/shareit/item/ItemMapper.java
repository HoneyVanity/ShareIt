package ru.yandex.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.yandex.practicum.shareit.item.dto.CreateItemDto;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item toItem(CreateItemDto dto);
}
