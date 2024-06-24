package ru.practicum.shareit.booking;

import org.hibernate.annotations.GeneratorType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Set;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private BookingService service;

    @PostMapping
    public Booking add(@RequestBody Booking booking) {
        return service.createBooking(booking);
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
