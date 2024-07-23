package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody CreateBookingDto createDto,
                                      @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return client.addBooking(bookerId, createDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirm(@PathVariable Long bookingId,
                                          @RequestParam Boolean approved,
                                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return client.confirmBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getListBookingsByUser(@RequestParam(defaultValue = "ALL") String state,
                                                              @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                              @RequestParam(required = false, defaultValue = "0") int from,
                                                              @RequestParam(required = false, defaultValue = "10") int size) {
        return client.getBookingListByBookerId(state, bookerId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getListBookingsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                               @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                               @RequestParam(required = false, defaultValue = "0") int from,
                                                               @RequestParam(required = false, defaultValue = "10") int size) {
        return client.getBookingListByOwnerId(state, ownerId, from, size);
    }
}
