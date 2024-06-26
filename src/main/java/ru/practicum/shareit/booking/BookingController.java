package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService service;

    @PostMapping
    public RequestBookingDto add(@RequestBody CreateBookingDto createDto,
                                 @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return service.createBooking(createDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public Booking confirm(@PathVariable Long bookingId,
                           @RequestParam Boolean approved) {
        return service.confirm(bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(@PathVariable Long bookingId) {
        return service.getBookingById(bookingId);
    }

    @GetMapping
    public List<Booking> getListBookings(@RequestParam(defaultValue = "ALL") String state,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getListBookingsByStatus(state, userId);
    }


}
