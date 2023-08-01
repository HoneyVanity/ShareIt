package ru.yandex.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.shareit.booking.Booking;
import ru.yandex.practicum.shareit.booking.BookingMapper;
import ru.yandex.practicum.shareit.booking.BookingJpaRepository;
import ru.yandex.practicum.shareit.booking.BookingStatus;
import ru.yandex.practicum.shareit.comment.CommentMapper;
import ru.yandex.practicum.shareit.comment.CommentJpaRepository;
import ru.yandex.practicum.shareit.comment.dto.CommentDto;
import ru.yandex.practicum.shareit.comment.dto.CreateCommentDto;
import ru.yandex.practicum.shareit.core.exception.NotFoundException;
import ru.yandex.practicum.shareit.item.dto.CreateItemDto;
import ru.yandex.practicum.shareit.item.dto.ItemDto;
import ru.yandex.practicum.shareit.item.dto.UpdateItemDto;
import ru.yandex.practicum.shareit.item.service.ItemService;
import ru.yandex.practicum.shareit.request.Request;
import ru.yandex.practicum.shareit.request.RequestJpaRepository;
import ru.yandex.practicum.shareit.user.User;
import ru.yandex.practicum.shareit.user.service.UserService;
import ru.yandex.practicum.shareit.utils.TestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    ItemJpaRepository repo;
    @Mock
    RequestJpaRepository requestRepo;

    @Mock
    BookingJpaRepository bookingRepo;

    @Mock
    CommentJpaRepository commentRepo;

    @Mock
    UserService userService;

    @Spy
    ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

    @Spy
    CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Spy
    BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @InjectMocks
    ItemService itemService;

    @Test
    void search_shouldReturnEmptyListIfTextIsBlank() {
        assertThat(itemService.searchByText("", null)).isEmpty();
    }

    @Test
    void search_shouldReturnListOfItems() {
        List<Item> items = List.of(
                TestUtils.makeItem(1L, true, null),
                TestUtils.makeItem(2L, true, null),
                TestUtils.makeItem(3L, true, null)
        );
        List<ItemDto> listOfItemDto = items
                .stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());

        when(repo.findAllByText(anyString(), any())).thenReturn(items);
        assertThat(itemService.searchByText("text", null)).isEqualTo(listOfItemDto);
    }

    @Test
    void create_shouldThrowNotFoundExceptionIfUserIsNotExists() {
        long userId = 1;
        when(userService.getById(userId)).thenThrow(NotFoundException.class);
        assertThatThrownBy(() -> itemService.create(userId, null)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldCreateItemWithRequest() {
        long userId = 1L;
        long requestId = 1L;
        User user = TestUtils.makeUser(1L);
        Request request = TestUtils.makeRequest(requestId, LocalDateTime.now(), user);
        CreateItemDto createItemDto = TestUtils.makeCreateItemDto(true, requestId);

        when(userService.getById(userId)).thenReturn(user);
        when(requestRepo.findById(userId)).thenReturn(Optional.of(request));
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto itemDto = itemService.create(userId, createItemDto);

        assertThat(itemDto.getRequestId()).isEqualTo(requestId);
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getComments()).isNull();
        assertThat(itemDto.getOwner()).isEqualTo(user);
    }

    @Test
    void update_shouldThrowNotFoundExceptionIfUserIsNotExists() {
        long itemId = 1L;
        long userId = 1L;

        when(userService.getById(userId)).thenThrow(NotFoundException.class);

        assertThatThrownBy(() -> itemService.update(itemId, userId, null)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldThrowNotFoundExceptionIfRequestIsNotExists() {
        long itemId = 1L;
        long userId = 1L;

        when(repo.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(itemId, userId, null)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldUpdateItemName() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        UpdateItemDto updateItemDto = new UpdateItemDto("new name", null, null);

        when(userService.getById(userId)).thenReturn(user);
        when(repo.findById(itemId)).thenReturn(Optional.of(item));
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto itemDto = itemService.update(itemId, userId, updateItemDto);

        assertThat(itemDto.getName()).isEqualTo("new name");
    }

    @Test
    void update_shouldUpdateItemDescription() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        UpdateItemDto updateItemDto = new UpdateItemDto(null, "new description", null);

        when(userService.getById(userId)).thenReturn(user);
        when(repo.findById(itemId)).thenReturn(Optional.of(item));
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto itemDto = itemService.update(itemId, userId, updateItemDto);

        assertThat(itemDto.getDescription()).isEqualTo("new description");
    }

    @Test
    void update_shouldUpdateItemAvailable() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        UpdateItemDto updateItemDto = new UpdateItemDto(null, null, false);

        when(userService.getById(userId)).thenReturn(user);
        when(repo.findById(itemId)).thenReturn(Optional.of(item));
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto itemDto = itemService.update(itemId, userId, updateItemDto);

        assertThat(itemDto.getAvailable()).isFalse();
    }

    @Test
    void comment_shouldCommentRequest() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        CreateCommentDto createCommentDto = new CreateCommentDto("new comment");

        when(userService.getById(userId)).thenReturn(user);
        when(repo.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                anyLong(),
                any(),
                any()
        )).thenReturn(List.of(new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.APPROVED)));
        when(commentRepo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        CommentDto commentDto = itemService.comment(itemId, userId, createCommentDto);

        assertThat(commentDto.getAuthorName()).isEqualTo(user.getName());
    }

    @Test
    void delete_shouldDeleteItemAndReturnDeletedItem() {
        User user = TestUtils.makeUser(1L);
        Item item = TestUtils.makeItem(1L, true, user);
        repo.delete(item);
        verify(repo, times(1)).delete(item);
    }

    @Test
    void getById_should() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);

        when(repo.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepo.findAllByItemIdOrderByStartAsc(anyLong())).thenReturn(Collections.emptyList());
        when(commentRepo.findAllByItemId(anyLong())).thenReturn(Collections.emptyList());

        assertThat(itemService.getById(itemId, userId)).isEqualTo(mapper.toItemDto(item));
    }
}
