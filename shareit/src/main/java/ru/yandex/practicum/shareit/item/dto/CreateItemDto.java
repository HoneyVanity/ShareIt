package ru.yandex.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemDto {
    @NotBlank(message = "Name is required")
    String name;

    @NotBlank(message = "Description is required")
    String description;

    @NotNull(message = "Available is required")
    Boolean available;

    @Positive(message = "RequestId must be positive")
    Long requestId;
}