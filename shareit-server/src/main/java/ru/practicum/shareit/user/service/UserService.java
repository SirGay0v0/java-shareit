package ru.practicum.shareit.user.service;


import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User update(Long userId, User user);

    void delete(Long userId);

    User getById(Long userId);

    List<User> getAll();
}
