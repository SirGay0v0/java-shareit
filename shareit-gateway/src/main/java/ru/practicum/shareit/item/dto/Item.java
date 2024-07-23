package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class Item {
    Long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    Long ownerId;
    @NotNull
    Boolean available;
    Long requestId;
}
