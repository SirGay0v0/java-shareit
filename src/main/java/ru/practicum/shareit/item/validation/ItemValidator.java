package ru.practicum.shareit.item.validation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

@Component
@AllArgsConstructor
public class ItemValidator {

    private final UserStorage userStorage;
    private final ItemStorage storage;

    public void createValidate(ItemRequest item, long ownerId) {
        if (userStorage.getById(ownerId) == null) {
            throw new NotFoundException("No such user with ID: " + ownerId);
        }

        if (item.getAvailable() == null) {
            throw new ValidationException("Invalid field value \"vailable\"");
        }

        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Invalid field value \"name\"");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Invalid field value \"description\"");
        }
    }

    public void updateValidate(ItemRequest item, long ownerId){

    }
}
