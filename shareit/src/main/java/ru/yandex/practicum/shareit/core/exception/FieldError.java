package ru.yandex.practicum.shareit.core.exception;

import lombok.Data;

@Data
public class FieldError {
    private final String field;
    private final String description;
}