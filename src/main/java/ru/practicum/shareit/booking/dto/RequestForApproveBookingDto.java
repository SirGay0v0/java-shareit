package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Status;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestForApproveBookingDto {
    Long id;
    Status status;
    UserBookingDto booker;
    ItemBookingDto item;
}
