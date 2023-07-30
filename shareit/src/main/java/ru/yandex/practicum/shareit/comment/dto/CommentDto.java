package ru.yandex.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Data;
import net.bytebuddy.implementation.bind.annotation.BindingPriority;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}