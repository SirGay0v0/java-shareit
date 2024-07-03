package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.validation.UserValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;
    private final UserValidator validator;
    private final ModelMapper mapper;

    @Override
    public UserRequestDto create(User user) {
        return mapper.map(
                storage.save(user),
                UserRequestDto.class);
    }

    @Override
    public UserRequestDto update(Long userId, User newUser) {
        User oldUser = validator.validateById(userId);
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
            oldUser.setEmail(validator.validateByEmail(newUser));
        }
        return mapper.map(
                storage.save(oldUser),
                UserRequestDto.class);
    }

    @Override
    public void delete(Long userId) {
        storage.deleteById(userId);
    }

    @Override
    public UserRequestDto getById(Long userId) {
        User user = validator.validateById(userId);
        return mapper.map(user, UserRequestDto.class);
    }

    @Override
    public List<UserRequestDto> getAll() {
        return storage.findAll().stream()
                .map(user -> mapper.map(user, UserRequestDto.class))
                .collect(Collectors.toList());
    }
}
