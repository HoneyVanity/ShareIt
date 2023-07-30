package ru.yandex.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.shareit.booking.dto.BookingDto;
import ru.yandex.practicum.shareit.core.exception.ExceptionsHandler;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    static final String USER_ID_HEADER = "X-Sharer-User-Id";
    final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(ExceptionsHandler.class)
                .build();
    }

    @Test
    void getAllByBookerTest() throws Exception {
        long bookerId = 1L;

        when(bookingService.getAllByBooker(anyLong(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings").header(USER_ID_HEADER, bookerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        long bookerId = 1L;

        when(bookingService.getAllByOwner(anyLong(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings/owner").header(USER_ID_HEADER, bookerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    void getByIdTest() throws Exception {
        long bookingId = 1L;
        long bookerId = 1L;
        Booking booking = new Booking(bookingId, null, null, null, null, null);

        when(bookingService.getById(anyLong(), anyLong())).thenReturn(booking);

        mockMvc.perform(get("/bookings/" + bookingId).header(USER_ID_HEADER, bookerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
    }

    @Test
    void createTest() throws Exception {
        long bookingId = 1L;
        long bookerId = 1L;

        BookingDto bookingDto = new BookingDto(1L, null, null);
        String json = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON).header(USER_ID_HEADER, bookerId).content(json))
                .andExpect(status().isBadRequest());

        Booking booking = new Booking(bookingId, null, null, null, null, null);
        bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now());
        json = objectMapper.writeValueAsString(bookingDto);

        when(bookingService.create(anyLong(), any())).thenReturn(booking);

        mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON).header(USER_ID_HEADER, bookerId).content(json))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
    }

    @Test
    void updateTest() throws Exception {
        long bookingId = 1L;
        long bookerId = 1L;
        Booking booking = new Booking(bookingId, null, null, null, null, null);

        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(booking);

        mockMvc.perform(patch("/bookings/" + bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, bookerId)
                        .queryParam("approved", "true")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
    }
}