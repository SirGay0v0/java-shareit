package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.validation.UserValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;
    private final UserValidator validator;
    private final ModelMapper mapper;

    @Override
    public UserRequestDto create(User user) {
        return mapper.map(storage.save(user), UserRequestDto.class);
    }

    @Override
    public UserRequestDto update(long userId, User newUser) {
        Optional<User> oldUser = storage.findById(userId);
        if (oldUser.isPresent()) {
            if (newUser.getName() != null) {
                oldUser.get().setName(newUser.getName());
            }
            if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.get().getEmail())) {
                validator.validate(newUser);
                oldUser.get().setEmail(newUser.getEmail());
            }
            return mapper.map(storage.save(oldUser.get()), UserRequestDto.class);
        } else {
            throw new NotFoundException("No such user with ID: " + userId);
        }
    }

    @Override
    public void delete(long userId) {
        storage.deleteById(userId);
    }

    @Override
    public UserRequestDto getById(long userId) {
        Optional<User> opt = storage.findById(userId);
        if (opt.isPresent()) {
            return mapper.map(opt.get(), UserRequestDto.class);
        } else throw new NotFoundException("Field id is incorrect");
    }

    @Override
    public List<UserRequestDto> getAll() {
        return storage.findAll().stream()
                .map(user -> mapper.map(user, UserRequestDto.class))
                .collect(Collectors.toList());
    }
}
