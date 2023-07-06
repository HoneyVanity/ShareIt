package ru.yandex.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.core.exception.FieldValidationException;
import ru.yandex.practicum.shareit.core.exception.NotFoundException;
import ru.yandex.practicum.shareit.item.Item;
import ru.yandex.practicum.shareit.item.dto.ItemMapper;
import ru.yandex.practicum.shareit.item.dao.ItemDao;
import ru.yandex.practicum.shareit.item.dto.ItemDto;
import ru.yandex.practicum.shareit.user.User;
import ru.yandex.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {

    ItemDao itemDao;
    UserService userService;
    ItemMapper itemMapper;

    @Override
    public List<ItemDto> searchByOwnerId(Long userId) {
        return itemDao.searchByOwnerId(userId)
                .stream()
                .map(item -> {
                    ItemDto itemDto = itemMapper.toItemDto(item);
                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> items = itemDao
                .searchByText(text);
        System.out.println(items);
        return items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

    }

    @Override
    public ItemDto getItemById(long id, Long userId) {

        Item item = Optional.ofNullable(itemDao.getItemById(id))
                .orElseThrow(() -> new NotFoundException("item", id));
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto dto) {

        Optional.ofNullable(userId).orElseThrow(
                () -> new FieldValidationException("userId", "empty"));

        if (userService.getAll().stream().noneMatch(u -> u.getId().equals(userId))) {
            throw new NotFoundException("userId", userId);
        }

        Optional.ofNullable(dto.getDescription())
                .orElseThrow(
                        () -> new FieldValidationException("description", "empty"));

        Optional.ofNullable(dto.getAvailable())
                .orElseThrow(
                        () -> new FieldValidationException("available", "empty"));

        if (dto.getName().isBlank()) {
            throw new FieldValidationException("name", "blank");
        }
        Item item = itemMapper.toItem(dto);
        item.setOwner(userService.getById(userId));

        return itemMapper.toItemDto(itemDao.addItem(item));
    }

    @Override
    public ItemDto updateItem(long id, Long userId, ItemDto dto) {
        User user = userService.getById(userId);

        Item item = Optional.ofNullable(itemDao.getItemById(id))
                .orElseThrow(() -> new NotFoundException("item", id));

        if (!user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("owner", userId);
        }

        if (dto.getName() != null) {
            item.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        return itemMapper.toItemDto(itemDao.updateItem(item));
    }

}
