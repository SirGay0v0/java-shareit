package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User update(long userId, User user);

    void delete(long userId);

    User getById(long userId);

    List<User> getAll();
}
