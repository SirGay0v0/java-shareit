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
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookingValidator {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final BookingStorage bookingStorage;

    public Item validateItem(CreateBookingDto createDto) {

        Optional<Item> itemOpt = itemStorage.findById(createDto.getItemId());

        if (itemOpt.isEmpty()) {
            throw new NotFoundException("No such item with ID: " + createDto.getItemId());
        } else {
            if (!itemOpt.get().getAvailable()) {
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
                    return itemOpt.get();
                }
            }
        }
    }

    public User validateBooker(Long bookerId) {
        Optional<User> userOpt = userStorage.findById(bookerId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("No such user with ID: " + bookerId);
        } else {
            return userOpt.get();
        }
    }

    public Booking validateBooking(Long bookingId) {
        Optional<Booking> bookingOpt = bookingStorage.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new NotFoundException("No such booking with ID: " + bookingId);
        } else {
            return bookingOpt.get();
        }
    }
}
