package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

@Getter
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class RequestForApproveBookingDto {
    Long id;
    Status status;
    UserBookingDto booker;
    ItemBookingDto item;
}
