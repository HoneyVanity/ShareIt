package ru.yandex.practicum.shareit.request;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.shareit.core.exception.ExceptionsHandler;
import ru.yandex.practicum.shareit.core.exception.NotFoundException;
import ru.yandex.practicum.shareit.request.dto.CreateRequestDto;
import ru.yandex.practicum.shareit.request.dto.RequestDto;
import ru.yandex.practicum.shareit.request.service.RequestService;
import ru.yandex.practicum.shareit.utils.TestUtils;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
class RequestControllerTest {
    static final String USER_ID_HEADER = "X-Sharer-User-Id";
    final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    MockMvc mockMvc;

    @Mock
    RequestService requestService;

    @InjectMocks
    RequestController requestController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .setControllerAdvice(ExceptionsHandler.class)
                .build();
    }

    @Test
    void createRequest_shouldReturnNewRequest() throws Exception {
        long userId = 1L;
        RequestDto request = TestUtils.makeRequestDto(1L);
        CreateRequestDto dto = new CreateRequestDto(request.getDescription());
        String json = objectMapper.writeValueAsString(dto);

        when(requestService.createRequest(dto, userId)).thenReturn(request);

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(request)));
    }

    @Test
    void getOwnRequests_shouldReturnListOfRequests() throws Exception {
        long userId = 1L;
        List<RequestDto> requests = List.of(
                TestUtils.makeRequestDto(1L),
                TestUtils.makeRequestDto(2L),
                TestUtils.makeRequestDto(3L)
        );

        when(requestService.getOwnRequests(userId)).thenReturn(requests);

        mockMvc.perform(get("/requests").header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));
    }

    @Test
    void getOtherRequests_shouldReturnListOfRequests() throws Exception {
        long userId = 1L;
        List<RequestDto> requests = List.of(
                TestUtils.makeRequestDto(1L),
                TestUtils.makeRequestDto(2L),
                TestUtils.makeRequestDto(3L)
        );

        when(requestService.getOtherRequests(anyLong(), any())).thenReturn(requests);

        mockMvc.perform(get("/requests/all").header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));
    }

    @Test
    void getById_shouldReturnListOfRequests() throws Exception {
        long userId = 1L;
        long requestId = 1L;
        RequestDto request = TestUtils.makeRequestDto(requestId);

        when(requestService.getById(requestId, userId)).thenReturn(request);

        mockMvc.perform(get("/requests/" + requestId).header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(request)));
    }

    @Test
    void getById_shouldReturnNotFound() throws Exception {
        long userId = 1L;
        long requestId = 1L;

        when(requestService.getById(requestId, userId)).thenThrow(new NotFoundException("request", requestId));

        mockMvc.perform(get("/requests/" + requestId).header(USER_ID_HEADER, userId))
                .andExpect(status().isNotFound());
    }
}