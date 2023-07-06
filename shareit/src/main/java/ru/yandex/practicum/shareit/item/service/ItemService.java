package ru.yandex.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {
    List<ItemDto> searchByOwnerId(Long userId);

    List<ItemDto> searchByText(String text);

    ItemDto getItemById(long id, Long userId);

    ItemDto createItem(Long userId, ItemDto dto);

    ItemDto updateItem(long id, Long userId, ItemDto dto);

}
