package ru.practicum.shareit.requests.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class RequestForUserDto {
    Long id;
    String description;
    LocalDateTime created;
    List<ResponseDto> items;
}
