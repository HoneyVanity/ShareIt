package ru.yandex.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
    @NotBlank(message = "Name is required")
    String name;

    @Email(message = "Email is incorrect")
    @NotBlank(message = "Email is required")
    String email;
}