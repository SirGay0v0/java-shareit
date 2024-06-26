package ru.practicum.shareit.user.validation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Component
@AllArgsConstructor
public class UserValidator {
    private final UserStorage storage;

    public void validate(User user) {
        if (storage.findByEmail(user.getEmail()).isPresent()) {
            throw new InternalServerException("User with email: " + user.getEmail() + " already exist");
        }
    }
}
