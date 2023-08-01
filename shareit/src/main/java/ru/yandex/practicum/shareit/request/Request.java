package ru.yandex.practicum.shareit.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Table
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String description;

    LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}