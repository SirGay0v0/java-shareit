package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Set;

public interface BookingService {

    Booking createBooking(Booking booking);

    Booking confirm(Long itemId, boolean approve);

    Booking getBookingById(Long bookingId);

    List<Booking> getListBookingsByStatus(String state, Long userId);
}
