package ru.yandex.practicum.shareit.core.exception;

public class DuplicatedEmailException extends RuntimeException {
    public DuplicatedEmailException(String email) {
        super("User with email = " + email + " already exists");
    }
}
