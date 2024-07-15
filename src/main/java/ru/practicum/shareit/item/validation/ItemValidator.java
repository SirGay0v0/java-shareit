package ru.practicum.shareit.item.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comments.dto.CreateCommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemValidator {

    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final BookingStorage bookingStorage;

    public void validateByExistingUser(Long ownerId) {
        userStorage.findById(ownerId).orElseThrow(
                () -> new NotFoundException("No such user with ID: " + ownerId)
        );
    }

    public Item validateItem(Long itemId) {
        return itemStorage.findById(itemId).orElseThrow(
                () -> new NotFoundException("No such item with ID: " + itemId)
        );
    }

    public void validateComment(CreateCommentDto comment, Long userId, Long itemId) {
        List<Booking> bookList = bookingStorage.findByItemId(itemId);
        List<Booking> bookingListCurrentUser = bookList.stream().filter(booking -> booking.getBooker().getId().equals(userId)).filter(booking -> booking.getStatus().equals(Status.APPROVED)).filter(booking -> booking.getStart().isBefore(LocalDateTime.now())).collect(Collectors.toList());

        if (bookingListCurrentUser.isEmpty()) {
            throw new ValidationException("This user didn't book this item anytime");
        }
        if (comment.getText().isBlank()) {
            throw new ValidationException("The comment must contains some text");
        }
    }
}
