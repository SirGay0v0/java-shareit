package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.validation.ItemValidator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
        return storage.save(item);
    }

    @Override
    public Item updateItem(long ownerId, long itemId, ItemRequestDto itemRequestDto) throws AccessDeniedException {
        Optional<Item> oldItemOpt = storage.findById(itemId);
        if (oldItemOpt.isPresent()) {
            if (oldItemOpt.get().getOwnerId().equals(ownerId)) {
                if (itemRequestDto.getName() != null) {
                    oldItemOpt.get().setName(itemRequestDto.getName());
                }
                if (itemRequestDto.getDescription() != null) {
                    oldItemOpt.get().setDescription(itemRequestDto.getDescription());
                }
                if (itemRequestDto.getAvailable() != null) {
                    oldItemOpt.get().setAvailable(itemRequestDto.getAvailable());
                }
            } else {
                throw new AccessDeniedException("User with ID " + ownerId + " don't have access to item with ID: " + itemId);
            }
        } else throw new NotFoundException("No such item with ID: " + ownerId);
        return storage.save(oldItemOpt.get());
    }

    @Override
    public Item getItemById(long itemId) {
        Optional<Item> item = storage.findById(itemId);
        if (item.isPresent()) {
            return item.get();
        } else throw new NotFoundException("No such item with ID: " + itemId);

    }

    @Override
    public List<ItemForOwnerDto> getAllById(Long ownerId) {
        List<ItemForOwnerDto> list = storage.findByOwnerId(ownerId).stream()
                .map(item -> mapper.map(item, ItemForOwnerDto.class))
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public List<Item> searchItems(String request) {
        if (!StringUtils.hasText(request)) {
            return Collections.emptyList();
        } else {
            return storage.findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCase(
                    request, request)
                    .stream()
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
        }
    }
}
