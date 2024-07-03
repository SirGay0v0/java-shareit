package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.comments.dto.RequestCommentDto;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemForOwnerDto {
    Long id;
    String name;
    String description;
    Boolean available;
    LastBookingDto lastBooking;
    NextBookingDto nextBooking;
    List<RequestCommentDto> comments;
}
