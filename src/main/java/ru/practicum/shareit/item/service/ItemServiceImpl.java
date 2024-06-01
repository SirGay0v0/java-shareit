package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.validation.ItemValidator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage storage;
    private final ModelMapper mapper;
    private final ItemValidator validator;

    @Override
    public Item addNewItem(long ownerId, ItemRequestDto itemRequestDto) {
        validator.createValidate(itemRequestDto, ownerId);
        Item item = mapper.map(itemRequestDto, Item.class);
        item.setOwnerId(ownerId);
        return storage.create(item);
    }

    @Override
    public Item updateItem(long ownerId, long itemId, ItemRequestDto itemRequestDto) {
        Item oldItem = storage.getById(itemId);
        if (oldItem.getOwnerId().equals(ownerId)) {
            if (itemRequestDto.getName() != null) {
                oldItem.setName(itemRequestDto.getName());
            }
            if (itemRequestDto.getDescription() != null) {
                oldItem.setDescription(itemRequestDto.getDescription());
            }
            if (itemRequestDto.getAvailable() != null) {
                oldItem.setAvailable(itemRequestDto.getAvailable());
            }
        } else throw new NotFoundException("User with ID " + ownerId + " don't have access to item with ID: " + itemId);
        return storage.update(oldItem);
    }

    @Override
    public Item getItemById(long itemId) {
        return storage.getById(itemId);
    }

    @Override
    public List<ItemForOwnerDto> getAllById(long ownerId) {
        return storage.getAllById(ownerId).stream()
                .map(item -> mapper.map(item, ItemForOwnerDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String request) {
        if (!StringUtils.hasText(request)) {
            return Collections.emptyList();
        } else {
            return storage.searchItems(request).stream()
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
        }
    }
}
