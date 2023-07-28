package ru.yandex.practicum.shareit.core.exception;

public class UnsupportedStatusException extends RuntimeException {
    public UnsupportedStatusException() {
        super("Unknown state: UNSUPPORTED_STATUS");
    }
}
