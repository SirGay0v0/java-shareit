package ru.practicum.shareit.item.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class NextBookingDto {
    private Long id;
    private Long bookerId;
}
