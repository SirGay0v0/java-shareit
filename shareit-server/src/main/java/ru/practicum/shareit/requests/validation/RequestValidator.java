package ru.practicum.shareit.requests.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.requests.storage.RequestStorage;
import ru.practicum.shareit.user.storage.UserStorage;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final UserStorage userStorage;
    private final RequestStorage requestStorage;


    public void validateById(Long ownerId) {
        userStorage.findById(ownerId).orElseThrow(
                () -> new NotFoundException("No such user with ID: " + ownerId)
        );
    }

    public void validateRequestAndId(Long requestId, Long userId) {
        validateById(userId);
        requestStorage.findById(requestId).orElseThrow(
                () -> new NotFoundException("No such request with ID: " + requestId)
        );
    }
}
