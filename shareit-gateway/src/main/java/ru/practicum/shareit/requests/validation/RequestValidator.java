package ru.practicum.shareit.requests.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.requests.dto.CreateRequestDto;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    public void createValidate(CreateRequestDto createDto) {
        if (createDto.getDescription() == null || createDto.getDescription().isBlank()) {
            throw new ValidationException("Incorrect field description");
        }
    }

    public void pageRequestValidate(int from, int size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Params from or size has wrong value!");
        }
    }
}
