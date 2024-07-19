package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.CreateRequestDto;
import ru.practicum.shareit.requests.dto.RequestForUserDto;
import ru.practicum.shareit.requests.model.Request;

import java.util.List;

public interface RequestService {

    Request create(CreateRequestDto createDto, Long authorId);

    List<RequestForUserDto> getAllById(Long authorId);

    List<RequestForUserDto> getAllFromOtherUsers(Long userId, int from, int size);

    RequestForUserDto getRequestById(Long requestId, Long userId);
}
