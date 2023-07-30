package ru.yandex.practicum.shareit.user;

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
import ru.yandex.practicum.shareit.user.dto.CreateUserDto;
import ru.yandex.practicum.shareit.user.dto.UpdateUserDto;
import ru.yandex.practicum.shareit.user.service.UserService;
import ru.yandex.practicum.shareit.utils.TestUtils;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    MockMvc mockMvc;

    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(ExceptionsHandler.class)
                .build();
    }

    @Test
    void getAll_shouldReturnListOfUsers() throws Exception {
        List<User> users = List.of(
                TestUtils.makeUser(1L),
                TestUtils.makeUser(2L),
                TestUtils.makeUser(3L)
        );

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    void getAll_shouldReturnEmptyList() throws Exception {
        List<User> users = Collections.emptyList();

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    void getById_shouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getById_shouldReturnNotFound() throws Exception {
        long userId = 1L;

        when(userService.getById(userId)).thenThrow(new NotFoundException("user", userId));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_shouldReturnUser() throws Exception {
        long userId = 1L;
        User user = TestUtils.makeUser(userId);

        when(userService.getById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    void create_shouldCreateUser() throws Exception {
        long userId = 1L;
        User user = TestUtils.makeUser(userId);
        CreateUserDto dto = new CreateUserDto(user.getName(), user.getEmail());
        String json = objectMapper.writeValueAsString(dto);

        when(userService.create(dto)).thenReturn(user);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    void update_shouldUpdateUser() throws Exception {
        long userId = 1L;
        User user = TestUtils.makeUser(userId);
        UpdateUserDto dto = new UpdateUserDto(user.getName(), user.getEmail());
        String json = objectMapper.writeValueAsString(dto);

        when(userService.update(userId, dto)).thenReturn(user);

        mockMvc.perform(patch("/users/" + userId).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    void update_shouldReturnNotFound() throws Exception {
        long userId = 1L;
        User user = TestUtils.makeUser(userId);
        UpdateUserDto dto = new UpdateUserDto(user.getName(), user.getEmail());
        String json = objectMapper.writeValueAsString(dto);

        when(userService.update(userId, dto)).thenThrow(new NotFoundException("user", userId));

        mockMvc.perform(patch("/users/" + userId).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturnNotFound() throws Exception {
        long userId = 1L;

        when(userService.delete(userId)).thenThrow(new NotFoundException("user", userId));

        mockMvc.perform(delete("/users/" + userId)).andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturnDeletedUser() throws Exception {
        long userId = 1L;
        User user = TestUtils.makeUser(userId);

        when(userService.delete(userId)).thenReturn(user);

        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }
}
