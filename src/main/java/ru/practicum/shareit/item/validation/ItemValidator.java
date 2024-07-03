package ru.practicum.shareit.item.validation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comments.dto.CreateCommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ItemValidator {

    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final BookingStorage bookingStorage;

    public void validateByExistingUser(Long ownerId) {
        Optional<User> userOpt = userStorage.findById(ownerId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("No such user with ID: " + ownerId);
        }
    }

    public Item validateItem(Long itemId) {
        Optional<Item> itemOpt = itemStorage.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("No such item with ID: " + itemId);
        }
        return itemOpt.get();
    }

    public void validateComment(CreateCommentDto comment, Long userId, Long itemId) {
        List<Booking> bookList = bookingStorage.findAllBookingByItemId(itemId);
        List<Booking> bookingListCurrentUser = bookList.stream()
                .filter(booking -> booking.getBooker().getId().equals(userId))
                .filter(booking -> booking.getStatus().equals(Status.APPROVED))
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        if (bookingListCurrentUser.isEmpty()) {
            throw new ValidationException("This user didn't book this item anytime");
        }
        if (comment.getText().isBlank()) {
            throw new ValidationException("The comment must contains some text");
        }
    }
}
