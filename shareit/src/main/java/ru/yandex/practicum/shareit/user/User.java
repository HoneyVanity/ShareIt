package ru.yandex.practicum.shareit.user;

import lombok.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @PositiveOrZero private Long id;

    @NotEmpty private String name;

    private String email;
}