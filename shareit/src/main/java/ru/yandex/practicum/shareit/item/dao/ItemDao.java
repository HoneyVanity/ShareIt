package ru.yandex.practicum.shareit.item.dao;

import ru.yandex.practicum.shareit.item.Item;

import java.util.List;

public interface ItemDao {
    Item addItem(Item item);

    List<Item> searchByOwnerId(Long ownerId);

    List<Item> searchByText(String text);

    Item updateItem(Item item);

    Item getItemById(long itemId);

}
