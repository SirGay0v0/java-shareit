package ru.practicum.shareit.booking.validation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@AllArgsConstructor
public class BookingValidator {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public void validate(Long itemId, CreateBookingDto createDto, Long bookerId) {

//        Optional<Item> itemOpt = itemStorage.findById(itemId);
//
//        if (userStorage.findById(bookerId).isEmpty()) {
//            throw new NotFoundException("No such user with ID: " + bookerId);
//        }
//        if (itemOpt.isEmpty()) {
//            throw new NotFoundException("No such item with ID: " + itemId);
//        }
//        if (!itemOpt.get().getAvailable()) {
//            throw new ValidationException("This item is unavailable to book");
//        }
//        if (createDto.getEnd() == null ||
//                createDto.getStart() == null ||
//                createDto.getEnd().isBefore(LocalDateTime.now()) ||
//                createDto.getEnd().isBefore(createDto.getStart()) ||
//                createDto.getEnd().equals(createDto.getStart()) ||
//                createDto.getStart().isBefore(LocalDateTime.now())) {
//            throw new ValidationException("Invalid field value end or start");
//        }
//        if (!itemStorage.getReferenceById(createDto.getItemId()).getAvailable()) {
//            throw new ValidationException("The item is unavailable");
//        }
    }
}
