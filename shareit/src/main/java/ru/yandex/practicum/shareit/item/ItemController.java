package ru.yandex.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.shareit.item.dto.ItemDto;
import ru.yandex.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemController {
    private final static String USER_ID_HEADER = "X-Sharer-User-Id";
    ItemService itemService;

    @GetMapping
    public List<ItemDto> getByUserId(
            @PositiveOrZero @RequestHeader(required = false, name = USER_ID_HEADER) Long userId) {
        return itemService.searchByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam(required = false) String text) {
        return itemService.searchByText(text);

    }

    @GetMapping("/{id}")
    public ItemDto getById(@PositiveOrZero @PathVariable long id,
                           @RequestHeader(required = false, name = USER_ID_HEADER) Long userId) {
        return itemService.getItemById(id, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(required = false, name = USER_ID_HEADER) Long userId,
                          @NonNull @RequestBody ItemDto dto) {
        return itemService.createItem(userId, dto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @PositiveOrZero @PathVariable long id, @RequestHeader(required = false, name = USER_ID_HEADER) Long userId,
            @NonNull @RequestBody ItemDto dto
    ) {
        return itemService.updateItem(id, userId, dto);
    }
}