package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addNewItem(Long ownerId, ItemRequestDto itemRequestDto);

    Item updateItem(Long ownerId, Long itemId, ItemRequestDto itemRequestDto) throws AccessDeniedException;

    ItemForOwnerDto getItemById(Long itemId, Long userId);

    List<ItemForOwnerDto> getAllById(Long ownerId);

    List<Item> searchItems(String request);
}
