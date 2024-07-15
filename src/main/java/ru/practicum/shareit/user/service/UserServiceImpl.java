package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.validation.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;
    private final UserValidator validator;

    @Override
    public User create(User user) {
        //validator.validateByEmail(user);
        return storage.save(user);
    }

    @Override
    public User update(Long userId, User newUser) {
        User oldUser = validator.validateById(userId);
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
            validator.validateByEmail(newUser);
            oldUser.setEmail(newUser.getEmail());
        }
        return storage.save(oldUser);
    }

    @Override
    public void delete(Long userId) {
        storage.deleteById(userId);
    }

    @Override
    public User getById(Long userId) {
        validator.validateById(userId);
        return storage.getReferenceById(userId);
    }

    @Override
    public List<User> getAll() {
        return storage.findAll();
    }
}
