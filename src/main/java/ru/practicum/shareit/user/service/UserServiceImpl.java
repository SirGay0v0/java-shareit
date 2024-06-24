package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.validation.UserValidator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;
    private final UserValidator validator;

    @Override
    public User create(User user) {
        validator.createValidate(user);
        return storage.save(user);
    }

    @Override
    public User update(long userId, User newUser) {
        Optional<User> oldUser = storage.findById(userId);
        if (oldUser.isPresent()) {
            if (newUser.getName() != null) {
                oldUser.get().setName(newUser.getName());
            }
            if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.get().getEmail())) {
                validator.updateValidate(newUser);
                oldUser.get().setEmail(newUser.getEmail());
            }
            return storage.save(oldUser.get());
        } else {
            throw new NotFoundException("No such user with ID: " + userId);
        }
    }

    @Override
    public void delete(long userId) {
        storage.deleteById(userId);
    }

    @Override
    public User getById(long userId) {
        Optional<User> opt = storage.findById(userId);
        if (opt.isPresent()) {
            return opt.get();
        } else throw new NotFoundException("Field id is incorrect");
    }

    @Override
    public List<User> getAll() {
        return storage.findAll();
    }
}
