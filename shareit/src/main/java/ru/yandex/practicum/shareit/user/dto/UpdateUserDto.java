package ru.yandex.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;

@Value
public class UpdateUserDto {
    String name;

    @Email(message = "Email is incorrect")
    String email;
}