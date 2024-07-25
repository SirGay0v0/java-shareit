package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.validation.BookingValidator;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

public class BookingClient extends BaseClient {

    private final BookingValidator validator;

    public BookingClient(RestTemplate rest, BookingValidator validator) {
        super(rest);
        this.validator = validator;
    }

    public ResponseEntity<Object> addBooking(long bookerId, CreateBookingDto createDto) {
        validator.validateCreateBooking(createDto);
        return post("", bookerId, createDto);
    }

    public ResponseEntity<Object> confirmBooking(long bookingId, boolean approved, long ownerId) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved=" + approved, ownerId, parameters);
    }

    public ResponseEntity<Object> getBookingById(long bookingId, long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingListByBookerId(String state, long bookerId, int from, int size) {
        validator.validatePage(from, size);
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state=" + state + "&from=" + from + "&size=" + size,
                bookerId, parameters);
    }

    public ResponseEntity<Object> getBookingListByOwnerId(String state, long ownerId, int from, int size) {
        validator.validatePage(from, size);
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state=" + state + "&from=" + from + "&size=" + size,
                ownerId, parameters);
    }
}
