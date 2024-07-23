package ru.practicum.shareit.booking.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BookingValidator {
    public void validateCreateBooking(CreateBookingDto createDto) {

        if (createDto.getEnd() == null ||
                createDto.getStart() == null ||
                createDto.getEnd().isBefore(LocalDateTime.now()) ||
                createDto.getEnd().isBefore(createDto.getStart()) ||
                createDto.getEnd().equals(createDto.getStart()) ||
                createDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Invalid field value end or start");
        }
    }

    public void validatePage(int from, int size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Params from or size has wrong value!");
        }
    }
}