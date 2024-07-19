package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.comments.dto.CreateCommentDto;
import ru.practicum.shareit.item.comments.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addNewItem(Long ownerId, ItemRequestDto itemRequestDto);

    Item updateItem(Long ownerId, Long itemId, ItemRequestDto itemRequestDto) throws AccessDeniedException;

    ItemForOwnerDto getItemById(Long itemId, Long userId);

    List<ItemForOwnerDto> getAllById(Long userId, int from, int size);

    List<Item> searchItems(String request, int from, int size);

    RequestCommentDto addComment(CreateCommentDto comment, Long userId, Long itemId);
}
