package ru.yandex.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.yandex.practicum.shareit.request.dto.CreateRequestDto;
import ru.yandex.practicum.shareit.request.dto.RequestDto;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    Request toRequest(CreateRequestDto dto);

    RequestDto toRequestDto(Request request);
}