package ru.yandex.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.shareit.booking.dto.BookingDto;
import ru.yandex.practicum.shareit.core.exception.FieldValidationException;
import ru.yandex.practicum.shareit.core.exception.NotFoundException;
import ru.yandex.practicum.shareit.core.exception.UnsupportedStatusException;
import ru.yandex.practicum.shareit.item.Item;
import ru.yandex.practicum.shareit.item.ItemJpaRepository;
import ru.yandex.practicum.shareit.item.service.ItemService;
import ru.yandex.practicum.shareit.user.User;
import ru.yandex.practicum.shareit.user.service.UserService;
import ru.yandex.practicum.shareit.utils.TestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    BookingJpaRepository repo;

    @Spy
    BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Mock
    UserService userService;

    @Mock
    ItemService itemService;

    @Mock
    ItemJpaRepository itemRepo;

    @InjectMocks
    BookingService bookingService;

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdOrderByStartDesc() {
        bookingService.getAllByBooker(1L, "ALL", null);
        verify(repo).findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        bookingService.getAllByBooker(1L, "CURRENT", null);
        verify(repo)
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndEndBeforeOrderByStartDesc() {
        bookingService.getAllByBooker(1L, "PAST", null);
        verify(repo)
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStartAfterOrderByStartDesc() {
        bookingService.getAllByBooker(1L, "FUTURE", null);
        verify(repo)
                .findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStatusOrderByStartDescWithStatusWaiting() {
        bookingService.getAllByBooker(1L, "WAITING", null);
        verify(repo)
                .findAllByBookerIdAndStatusOrderByStartDesc(1, BookingStatus.WAITING, null);
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStatusOrderByStartDescWithStatusRejected() {
        bookingService.getAllByBooker(1L, "REJECTED", null);
        verify(repo)
                .findAllByBookerIdAndStatusOrderByStartDesc(1L, BookingStatus.REJECTED, null);
    }

    @Test
    void getAllByBooker_shouldThrowUnsupportedStatusException() {
        assertThatThrownBy(() -> bookingService.getAllByBooker(1L, "ANY", null)).isInstanceOf(UnsupportedStatusException.class);
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdOrderByStartDesc() {
        bookingService.getAllByOwner(1L, "ALL", null);
        verify(repo).findAllByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        bookingService.getAllByOwner(1L, "CURRENT", null);
        verify(repo)
                .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        bookingService.getAllByOwner(1L, "PAST", null);
        verify(repo)
                .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        bookingService.getAllByOwner(1L, "FUTURE", null);
        verify(repo)
                .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStatusOrderByStartDescWithStatusWaiting() {
        bookingService.getAllByOwner(1L, "WAITING", null);
        verify(repo)
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(1, BookingStatus.WAITING, null);
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStatusOrderByStartDescWithStatusRejected() {
        bookingService.getAllByOwner(1L, "REJECTED", null);
        verify(repo)
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(1, BookingStatus.REJECTED, null);
    }

    @Test
    void getAllByOwner_shouldThrowUnsupportedStatusException() {
        assertThatThrownBy(() -> bookingService.getAllByOwner(1L, "ANY", null)).isInstanceOf(UnsupportedStatusException.class);
    }

    @Test
    void create_shouldThrowNotFoundIfUserIsNotExists() {
        long userId = 1L;
        long itemId = 1L;
        BookingDto dto = new BookingDto(itemId, LocalDateTime.now(), LocalDateTime.now());

        when(userService.getById(userId)).thenThrow(new NotFoundException("user", userId));

        assertThatThrownBy(() -> bookingService.create(userId, dto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldThrowNotFoundIfItemIsNotExists() {
        long userId = 1L;
        long itemId = 1L;
        BookingDto dto = new BookingDto(itemId, LocalDateTime.now(), LocalDateTime.now());

        when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(userId, dto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldThrowFieldValidationExceptionIfItemIsUnavailable() {
        long userId = 1L;
        long itemId = 1L;
        BookingDto dto = new BookingDto(itemId, LocalDateTime.now(), LocalDateTime.now());
        Item item = TestUtils.makeItem(itemId, false, null);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(userId, dto)).isInstanceOf(FieldValidationException.class);
    }

    @Test
    void create_shouldThrowNotFoundIfUserIsNotOwner() {
        long userId = 1L;
        long itemId = 1L;
        BookingDto dto = new BookingDto(itemId, LocalDateTime.now(), LocalDateTime.now());
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(userId, dto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldThrowFieldValidationExceptionIfDateIsIncorrect() {
        long userId = 1L;
        long itemId = 1L;
        BookingDto dto = new BookingDto(itemId, LocalDateTime.now(), LocalDateTime.now().minusDays(1));
        User user = TestUtils.makeUser(2L);
        Item item = TestUtils.makeItem(itemId, true, user);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(userId, dto)).isInstanceOf(FieldValidationException.class);
    }

    @Test
    void create_shouldCreateBooking() {
        long userId = 1L;
        long itemId = 1L;
        BookingDto dto = new BookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        User user = TestUtils.makeUser(2L);
        Item item = TestUtils.makeItem(itemId, true, user);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Booking booking = bookingService.create(userId, dto);

        assertThat(booking).hasFieldOrProperty("id");
    }

    @Test
    void update_shouldThrowNotFoundIfBookingIsNotExists() {
        long bookingId = 1L;
        long userId = 1L;

        when(repo.findById(bookingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.update(bookingId, userId, true)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldThrowNotFoundIfUserIsNotOwner() {
        long bookingId = 1L;
        long userId = 1L;
        long itemId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.WAITING);

        when(repo.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.update(bookingId, 2L, true)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldThrowFieldValidationExceptionIfBookingIsAlreadyApproved() {
        long bookingId = 1L;
        long userId = 1L;
        long itemId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.APPROVED);

        when(repo.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.update(bookingId, userId, true)).isInstanceOf(FieldValidationException.class);
    }

    @Test
    void update_shouldUpdateBookingToApproved() {
        long bookingId = 1L;
        long userId = 1L;
        long itemId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.WAITING);

        when(repo.findById(bookingId)).thenReturn(Optional.of(booking));
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        booking = bookingService.update(bookingId, userId, true);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionIfBookingIsNotExists() {
        long bookingId = 1L;
        long userId = 1L;

        when(repo.findById(bookingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getById(bookingId, userId)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionIfUserIsNotOwner() {
        long bookingId = 1L;
        long userId = 1L;
        long itemId = 1L;

        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.WAITING);

        when(repo.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getById(bookingId, 2L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_shouldReturnBooking() {
        long bookingId = 1L;
        long userId = 1L;
        long itemId = 1L;

        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.WAITING);

        when(repo.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThat(bookingService.getById(bookingId, userId)).isEqualTo(booking);
    }
}