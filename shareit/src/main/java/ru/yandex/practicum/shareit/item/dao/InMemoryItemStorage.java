package ru.yandex.practicum.shareit.item.dao;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.shareit.item.Item;
import ru.yandex.practicum.shareit.request.ItemRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryItemStorage implements ItemDao {

    Long id = 0L;
    Map<Long, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        id = id + 1L;
        item.setId(id);
        item.setRequest(new ItemRequest());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> searchByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchByText(String text) {

        return items.values()
                .stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text.toLowerCase()) &&
                        i.isAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(Item itemDto) {

        Item item = items.get(itemDto.getId());

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.isAvailable());
        item.setOwner(itemDto.getOwner());

        return item;
    }

    @Override
    public Item getItemById(long itemId) {

        return items.get(itemId);
    }
}
