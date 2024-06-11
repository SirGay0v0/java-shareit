package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item create(Item item);

    Item update(Item item);

    Item getById(long itemId);

    List<Item> getAllById(long ownerId);

    List<Item> searchItems(String request);
}
