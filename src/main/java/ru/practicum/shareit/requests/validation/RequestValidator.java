package ru.practicum.shareit.requests.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.requests.dto.CreateRequestDto;
import ru.practicum.shareit.requests.storage.RequestStorage;
import ru.practicum.shareit.user.storage.UserStorage;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final UserStorage userStorage;
    private final RequestStorage requestStorage;

    public void createValidate(Long ownerId, CreateRequestDto createDto) {
        validateById(ownerId);

        if (createDto.getDescription() == null || createDto.getDescription().isBlank()) {
            throw new ValidationException("Incorrect field description");
        }
    }

    public void validateById(Long ownerId) {
        userStorage.findById(ownerId).orElseThrow(
                () -> new NotFoundException("No such user with ID: " + ownerId)
        );
    }

    public PageRequest createPageAndValidateUser(int from, int size, Long ownerId) {
        validateById(ownerId);
        if (from < 0 || size < 1) {
            throw new ValidationException("Params from or size has wrong value!");
        }
        return PageRequest.of(from / size, size);
    }

    public void validateRequestAndId(Long requestId, Long userId) {
        validateById(userId);
        requestStorage.findById(requestId).orElseThrow(
                () -> new NotFoundException("No such request with ID: " + requestId)
        );
    }
}
