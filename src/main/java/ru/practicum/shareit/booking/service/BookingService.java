package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;

import java.util.List;

public interface BookingService {

    RequestBookingDto createBooking(CreateBookingDto createDto, Long bookerId);

    RequestBookingDto confirm(Long itemId, boolean approve, Long ownerId);

    RequestBookingDto getBookingById(Long bookingId, Long userId);

    List<RequestBookingDto> getListUserBookingsByStatus(String state, Long userId);

    List<RequestBookingDto> getListOwnerBookingsByStatus(String state, Long ownerId);

}
