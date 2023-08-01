package ru.yandex.practicum.shareit.comment.dto;

import lombok.*;
import net.bytebuddy.implementation.bind.annotation.BindingPriority;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class CommentDto {
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}