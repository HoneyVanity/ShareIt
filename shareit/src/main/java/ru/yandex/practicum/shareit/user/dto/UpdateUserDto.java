package ru.yandex.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    String name;

    @Email(message = "Email is incorrect")
    String email;
}