package ru.practicum.shareit.item.comments.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestCommentDto {
    Long id;
    String text;
    String authorName;
    LocalDateTime created;
}
