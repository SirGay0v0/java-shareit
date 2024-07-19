package ru.practicum.shareit.booking.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BookingValidator {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final BookingStorage bookingStorage;

    public Item validateItem(CreateBookingDto createDto) {

        Item item = itemStorage.findById(createDto.getItemId()).orElseThrow(
                () -> new NotFoundException("No such item with ID: " + createDto.getItemId()));

        if (!item.getAvailable()) {
            throw new ValidationException("This item is unavailable to book");
        } else {
            if (createDto.getEnd() == null ||
                    createDto.getStart() == null ||
                    createDto.getEnd().isBefore(LocalDateTime.now()) ||
                    createDto.getEnd().isBefore(createDto.getStart()) ||
                    createDto.getEnd().equals(createDto.getStart()) ||
                    createDto.getStart().isBefore(LocalDateTime.now())) {
                throw new ValidationException("Invalid field value end or start");
            } else {
                return item;
            }
        }

    }

    public User validateBooker(Long bookerId) {
        return userStorage.findById(bookerId).orElseThrow(
                () -> new NotFoundException("No such user with ID: " + bookerId));
    }

    public Booking validateBooking(Long bookingId) {
        return bookingStorage.findById(bookingId).orElseThrow(
                () -> new NotFoundException("No such booking with ID: " + bookingId));
    }

    public void validatePage(int from, int size, Long bookerId) {
        validateBooker(bookerId);
        if (from < 0 || size < 1) {
            throw new ValidationException("Params from or size has wrong value!");
        }
    }
}
