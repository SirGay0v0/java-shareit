package ru.practicum.shareit.user.service;


import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserRequestDto create(User user);

    UserRequestDto update(Long userId, User user);

    void delete(Long userId);

    UserRequestDto getById(Long userId);

    List<UserRequestDto> getAll();
}
