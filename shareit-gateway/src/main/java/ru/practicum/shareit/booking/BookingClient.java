package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.validation.BookingValidator;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";
    private final BookingValidator validator;

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder, BookingValidator validator) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
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
