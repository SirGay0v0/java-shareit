package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class ItemRequestDto {
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
}
