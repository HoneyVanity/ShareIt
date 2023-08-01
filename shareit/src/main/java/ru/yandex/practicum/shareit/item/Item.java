package ru.yandex.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.shareit.booking.dto.ShortBookingDto;
import ru.yandex.practicum.shareit.comment.dto.CommentDto;
import ru.yandex.practicum.shareit.request.Request;
import ru.yandex.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString
@Table
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String description;

    Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    User owner;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @Transient
    ShortBookingDto lastBooking;

    @Transient
    ShortBookingDto nextBooking;

    @Transient
    List<CommentDto> comments;
}