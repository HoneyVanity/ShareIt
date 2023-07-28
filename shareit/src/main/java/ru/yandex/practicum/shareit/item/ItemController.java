package ru.yandex.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.shareit.comment.dto.CommentDto;
import ru.yandex.practicum.shareit.comment.dto.CreateCommentDto;
import ru.yandex.practicum.shareit.item.dto.CreateItemDto;
import ru.yandex.practicum.shareit.item.dto.UpdateItemDto;
import ru.yandex.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemController {
    private final static String USER_ID_HEADER = "X-Sharer-User-Id";
    ItemService service;

    @GetMapping
    public List<Item> getByUserId(@RequestHeader(required = false, name = USER_ID_HEADER) Long userId) {
        return service.getByUserId(userId);
    }

    @GetMapping("/search")
    public List<Item> search(
            @RequestParam(required = false) String text) {
        return service.searchByText(text);

    }

    @GetMapping("/{id}")
    public Item getById(@PathVariable long id,
                        @RequestHeader(required = false, name = USER_ID_HEADER) Long userId) {
        return service.getById(id, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestHeader(required = false, name = USER_ID_HEADER) Long userId,
                       @Valid @RequestBody CreateItemDto dto) {
        return service.create(userId, dto);
    }

    @PatchMapping("/{id}")
    public Item update(
            @PathVariable long id, @RequestHeader(required = false, name = USER_ID_HEADER) Long userId,
            @Valid @RequestBody UpdateItemDto dto
    ) {
        return service.update(id, userId, dto);
    }

    @DeleteMapping("/{id}")
    public Item delete(@PathVariable long id) {
        return service.delete(id);
    }

    @PostMapping("/{id}/comment")
    public CommentDto comment(
            @PathVariable long id,
            @RequestHeader(name = USER_ID_HEADER) long userId,
            @Valid @RequestBody CreateCommentDto dto
    ) {
        return service.comment(id, userId, dto);
    }
}