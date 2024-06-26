package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Set;

public interface BookingService {

    RequestBookingDto createBooking(CreateBookingDto createDto, Long bookerId);

    Booking confirm(Long itemId, boolean approve);

    Booking getBookingById(Long bookingId);

    List<Booking> getListBookingsByStatus(String state, Long userId);
}
