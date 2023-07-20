package ru.yandex.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.shareit.item.Item;
@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                0L
        );
    }

    public Item toItem(ItemDto dto) {
        return new Item(null,
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                null,
                null
        );
    }
}
