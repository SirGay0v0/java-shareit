package ru.practicum.shareit.item.validation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.persistence.EntityNotFoundException;
import javax.swing.text.html.parser.Entity;
import javax.validation.ConstraintViolationException;

@Component
@AllArgsConstructor
public class ItemValidator {

    private final UserStorage userStorage;

    public void createValidate(ItemRequestDto item, long ownerId) {
        try {
            userStorage.getReferenceById(ownerId).getName();
        }catch (EntityNotFoundException ex){
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
}
