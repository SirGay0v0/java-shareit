package ru.practicum.shareit.user.validation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class UserValidator {
    private final UserStorage storage;

    public void createValidate(User user) {
        List<User> list = storage.getAllUsers();

        for (User users : list) {
            if (users.getEmail().equals(user.getEmail())) {
                throw new InternalServerException("User with email: " + user.getEmail() + " already exist");
            }
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Invalid field value \"email\"");
        }

        Pattern pattern = Pattern.compile("^.+@.+.com");
        Matcher matcher = pattern.matcher(user.getEmail());
        if (!matcher.find()) {
            throw new ValidationException("Invalid field value \"email\"");
        }
    }

    public void updateValidate(User user) {
        List<User> list = storage.getAllUsers();
        for (User users : list) {
            if (users.getEmail().equals(user.getEmail())) {
                throw new InternalServerException("User with email: " + user.getEmail() + " already exist");
            }
        }
    }
}
