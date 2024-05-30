package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemForOwner;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addNewItem(long ownerId, ItemRequest itemRequest);

    Item updateItem(long ownerId, long itemId, ItemRequest itemRequest);

    Item getItemById(long itemId);

    List<ItemForOwner> getAllById(long ownerId);

    List<Item> searchItems(String request);
}
