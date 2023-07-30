package ru.yandex.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.shareit.comment.dto.CommentDto;
import ru.yandex.practicum.shareit.comment.dto.CreateCommentDto;
import ru.yandex.practicum.shareit.core.exception.ExceptionsHandler;
import ru.yandex.practicum.shareit.core.exception.NotFoundException;
import ru.yandex.practicum.shareit.item.dto.CreateItemDto;
import ru.yandex.practicum.shareit.item.dto.ItemDto;
import ru.yandex.practicum.shareit.item.dto.UpdateItemDto;
import ru.yandex.practicum.shareit.item.service.ItemService;
import ru.yandex.practicum.shareit.utils.TestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    static final String USER_ID_HEADER = "X-Sharer-User-Id";
    final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    MockMvc mockMvc;

    @Mock
    ItemService itemService;

    @Spy
    ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @InjectMocks
    ItemController itemController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(ExceptionsHandler.class)
                .build();
    }

    @Test
    void getByUserId_shouldReturnListOfItems() throws Exception {
        long userId = 1;
        List<ItemDto> items = Stream.of(
                TestUtils.makeItem(1L, true, null),
                TestUtils.makeItem(2L, true, null),
                TestUtils.makeItem(3L, true, null)
        ).map(itemMapper::toItemDto).collect(Collectors.toList());

        when(itemService.getByUserId(anyLong(), any())).thenReturn(items);

        mockMvc.perform(get("/items").header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @Test
    void getByUserId_shouldReturnNotFound() throws Exception {
        long userId = 1L;

        when(itemService.getByUserId(anyLong(), any())).thenThrow(new NotFoundException("user", userId));

        mockMvc.perform(get("/items").header(USER_ID_HEADER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByUserId_shouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/items")).andExpect(status().isInternalServerError());
    }

    @Test
    void search_shouldReturnListOfItems() throws Exception {
        List<ItemDto> items = Stream.of(
                TestUtils.makeItem(1L, true, null),
                TestUtils.makeItem(2L, true, null),
                TestUtils.makeItem(3L, true, null)
        ).map(itemMapper::toItemDto).collect(Collectors.toList());

        when(itemService.searchByText(anyString(), any())).thenReturn(items);

        mockMvc.perform(get("/items/search").queryParam("text", "test"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @Test
    void getById_shouldReturnItem() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        ItemDto item = itemMapper.toItemDto(TestUtils.makeItem(itemId, true, null));

        when(itemService.getById(itemId, userId)).thenReturn(item);

        mockMvc.perform(get("/items/" + itemId).header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));
    }

    @Test
    void getById_shouldReturnNotFound() throws Exception {
        long itemId = 1L;
        long userId = 1L;

        when(itemService.getById(itemId, userId)).thenThrow(new NotFoundException("item", itemId));

        mockMvc.perform(get("/items/" + itemId).header(USER_ID_HEADER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldCreateAndReturnNewItem() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        ItemDto item = itemMapper.toItemDto(TestUtils.makeItem(itemId, true, null));
        CreateItemDto dto = TestUtils.makeCreateItemDto(true, 1L);
        String json = objectMapper.writeValueAsString(dto);

        when(itemService.create(userId, dto)).thenReturn(item);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));
    }

    @Test
    void update_shouldUpdateItem() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        ItemDto item = itemMapper.toItemDto(TestUtils.makeItem(itemId, true, null));
        UpdateItemDto dto = new UpdateItemDto(item.getName(), item.getDescription(), item.getAvailable());
        String json = objectMapper.writeValueAsString(dto);

        when(itemService.update(itemId, userId, dto)).thenReturn(item);

        mockMvc.perform(patch("/items/" + itemId)
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));
    }

    @Test
    void update_shouldReturnNotFound() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        ItemDto item = itemMapper.toItemDto(TestUtils.makeItem(itemId, true, null));
        UpdateItemDto dto = new UpdateItemDto(item.getName(), item.getDescription(), item.getAvailable());
        String json = objectMapper.writeValueAsString(dto);

        when(itemService.update(itemId, userId, dto)).thenThrow(new NotFoundException("item", itemId));

        mockMvc.perform(patch("/items/" + itemId)
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturnDeletedItem() throws Exception {
        long itemId = 1L;
        ItemDto item = itemMapper.toItemDto(TestUtils.makeItem(itemId, true, null));

        when(itemService.delete(itemId)).thenReturn(item);

        mockMvc.perform(delete("/items/" + itemId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));
    }

    @Test
    void delete_shouldReturnNotFound() throws Exception {
        long itemId = 1L;

        when(itemService.delete(itemId)).thenThrow(new NotFoundException("item", itemId));

        mockMvc.perform(delete("/items/" + itemId))
                .andExpect(status().isNotFound());
    }

    @Test
    void comment_shouldReturnNotFound() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        CreateCommentDto dto = new CreateCommentDto("text");
        CommentDto comment = CommentDto.builder()
                .id(1L)
                .authorName("test name")
                .text("comment")
                .created(LocalDateTime.now())
                .build();
        String json = objectMapper.writeValueAsString(dto);

        when(itemService.comment(itemId, userId, dto)).thenReturn(comment);

        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(comment)));
    }
}
