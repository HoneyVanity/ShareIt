package ru.yandex.practicum.shareit.item.dto;

import jdk.jfr.BooleanFlag;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    @PositiveOrZero Long id;
    @NotEmpty String name;
    @NotEmpty String description;
    @BooleanFlag
    Boolean available;
    @PositiveOrZero Long requestId;
}
