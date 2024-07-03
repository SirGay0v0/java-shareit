package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemBookingDto {
    Long id;
    String name;
}
