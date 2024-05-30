package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForOwner;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.validation.ItemValidator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage storage;
    private final ModelMapper mapper;
    private final ItemValidator validator;

    @Override
    public Item addNewItem(long ownerId, ItemRequest itemRequest) {
        validator.createValidate(itemRequest, ownerId);
        Item item = mapper.map(itemRequest, Item.class);
        item.setOwnerId(ownerId);
        return storage.create(item);
    }

    @Override
    public Item updateItem(long ownerId, long itemId, ItemRequest itemRequest) {
        Item oldItem = storage.getById(itemId);
        if (oldItem.getOwnerId().equals(ownerId)) {
            if (itemRequest.getName() != null) {
                oldItem.setName(itemRequest.getName());
            }
            if (itemRequest.getDescription() != null) {
                oldItem.setDescription(itemRequest.getDescription());
            }
            if (itemRequest.getAvailable() != null) {
                oldItem.setAvailable(itemRequest.getAvailable());
            }
        } else throw new NotFoundException("User with ID " + ownerId + " don't have access to item with ID: " + itemId);
        return storage.update(oldItem);
    }

    @Override
    public Item getItemById(long itemId) {
        return storage.getById(itemId);
    }

    @Override
    public List<ItemForOwner> getAllById(long ownerId) {
        return storage.getAllById(ownerId).stream()
                .map(item -> mapper.map(item, ItemForOwner.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String request) {
        if (request.isBlank() || request.isEmpty()) {
            return Collections.emptyList();
        } else {
            return storage.searchItems(request).stream()
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
        }
    }
}
