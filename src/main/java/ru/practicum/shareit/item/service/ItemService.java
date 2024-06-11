package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addNewItem(long ownerId, ItemRequestDto itemRequestDto);

    Item updateItem(long ownerId, long itemId, ItemRequestDto itemRequestDto);

    Item getItemById(long itemId);

    List<ItemForOwnerDto> getAllById(long ownerId);

    List<Item> searchItems(String request);
}
