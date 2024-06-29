package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

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
}
