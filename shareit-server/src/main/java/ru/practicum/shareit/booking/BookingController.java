package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

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
    public RequestBookingDto confirm(@PathVariable Long bookingId,
                                     @RequestParam Boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return service.confirm(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public RequestBookingDto getBookingById(@PathVariable Long bookingId,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<RequestBookingDto> getListBookingsByUser(@RequestParam(defaultValue = "ALL") String state,
                                                         @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                         @RequestParam(required = false, defaultValue = "0") int from,
                                                         @RequestParam(required = false, defaultValue = "10") int size) {
        return service.getListUserBookingsByStatus(state, bookerId, from, size);
    }

    @GetMapping("/owner")
    public List<RequestBookingDto> getListBookingsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                          @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                          @RequestParam(required = false, defaultValue = "0") int from,
                                                          @RequestParam(required = false, defaultValue = "10") int size) {
        return service.getListOwnerBookingsByStatus(state, ownerId, from, size);
    }

}
