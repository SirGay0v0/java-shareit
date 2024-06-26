package ru.practicum.shareit.user.service;


import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserRequestDto create(User user);

    UserRequestDto update(long userId, User user);

    void delete(long userId);

    UserRequestDto getById(long userId);

    List<UserRequestDto> getAll();
}
