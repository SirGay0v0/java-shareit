package ru.practicum.shareit.requests.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class ResponseDto {
    Long id;
    String name;
    String description;
    Long requestId;
    Boolean available;
}
