package ru.yandex.practicum.shareit.request.service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.core.exception.NotFoundException;
import ru.yandex.practicum.shareit.item.ItemJpaRepository;
import ru.yandex.practicum.shareit.item.ItemMapper;
import ru.yandex.practicum.shareit.item.dto.ItemDto;
import ru.yandex.practicum.shareit.request.Request;
import ru.yandex.practicum.shareit.request.RequestMapper;
import ru.yandex.practicum.shareit.request.RequestJpaRepository;
import ru.yandex.practicum.shareit.request.dto.CreateRequestDto;
import ru.yandex.practicum.shareit.request.dto.RequestDto;
import ru.yandex.practicum.shareit.user.User;
import ru.yandex.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestMapper mapper;
    private final RequestJpaRepository repo;
    private final ItemJpaRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;

    public RequestDto createRequest(CreateRequestDto dto, long userId) {
        User user = userService.getById(userId);

        Request request = mapper.createRequestDtoToRequest(dto);
        request.setUser(user);
        request.setCreated(LocalDateTime.now());
        request = repo.save(request);

        return mapper.requestToRequestDto(request);
    }

    public List<RequestDto> getOwnRequests(long userId) {
        userService.getById(userId);

        return repo.findAllByUserIdOrderByCreatedDesc(userId)
                .stream()
                .map(this::toRequestDto)
                .collect(Collectors.toList());
    }

    public List<RequestDto> getOtherRequests(long userId, Pageable pageable) {
        userService.getById(userId);

        return repo.findAllByUserIdIsNotOrderByCreatedDesc(userId, pageable)
                .stream()
                .map(this::toRequestDto)
                .collect(Collectors.toList());
    }

    public RequestDto getById(long requestId, long userId) {
        userService.getById(userId);

        Request request = repo
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException("request", requestId));

        return toRequestDto(request);
    }

    private RequestDto toRequestDto(Request request) {
        RequestDto requestDto = mapper.requestToRequestDto(request);
        List<ItemDto> items = itemRepository
                .findAllByRequestId(request.getId())
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        requestDto.setItems(items);

        return requestDto;
    }
}
