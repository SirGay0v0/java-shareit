package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.validation.UserValidator;

import java.util.List;

@Component
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    private final UserValidator validator;

    @Override
    public User create(User user) {
        validator.createValidate(user);
        return storage.create(user);
    }

    @Override
    public User update(long userId, User newUser) {
        User oldUser = storage.getById(userId);
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
            validator.updateValidate(newUser);
            oldUser.setEmail(newUser.getEmail());
        }
        return storage.update(oldUser);
    }

    @Override
    public void delete(long userId) {
        storage.delete(userId);
    }

    @Override
    public User getById(long userId) {
        return storage.getById(userId);
    }

    @Override
    public List<User> getAll() {
        return storage.getAllUsers();
    }
}
