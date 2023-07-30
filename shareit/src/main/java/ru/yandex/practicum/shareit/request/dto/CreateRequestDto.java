package ru.yandex.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRequestDto {
    @NotBlank(message = "Description is required")
    String description;
}