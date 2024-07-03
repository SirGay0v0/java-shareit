package ru.practicum.shareit.user.validation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Optional;

@Component
@AllArgsConstructor
public class UserValidator {
    private final UserStorage storage;

    public String validateByEmail(User user) {
        if (storage.findByEmail(user.getEmail()).isPresent()) {
            throw new InternalServerException("User with email: " + user.getEmail() + " already exist");
        } else {
            return user.getEmail();
        }
    }

    public User validateById(Long userId) {
        Optional<User> userOpt = storage.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("No such User with id " + userId);
        } else {
            return userOpt.get();
        }
    }
}
