package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.LastBookingDto;
import ru.practicum.shareit.item.dto.NextBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.validation.ItemValidator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage storage;
    private final ModelMapper mapper;
    private final ItemValidator validator;
    private final BookingStorage bookingStorage;

    @Override
    public Item addNewItem(Long ownerId, ItemRequestDto itemRequestDto) {
        validator.validateByExistingUser(ownerId);
        Item item = mapper.map(itemRequestDto, Item.class);
        item.setOwnerId(ownerId);
        return storage.save(item);
    }

    @Override
    public Item updateItem(Long ownerId, Long itemId, ItemRequestDto itemRequestDto) throws AccessDeniedException {
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
    public ItemForOwnerDto getItemById(Long itemId, Long userId) {
        Optional<Item> item = storage.findById(itemId);
        if (item.isPresent()) {

            ItemForOwnerDto resultItem = mapper.map(item, ItemForOwnerDto.class);
            List<Booking> bookList = bookingStorage.findAllBookingByItemId(item.get().getId());
            LocalDateTime timeNow = LocalDateTime.now();
            if (!bookList.isEmpty() && item.get().getOwnerId().equals(userId)) {
                List<Booking> sortedBookList = bookList.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now().minusSeconds(5)))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());

                NextBookingDto nextBooking = mapper.map(sortedBookList.get(0), NextBookingDto.class);
                LastBookingDto lastBooking = mapper.map(sortedBookList.get(sortedBookList.size() - 1), LastBookingDto.class);

                resultItem.setNextBooking(nextBooking);
                resultItem.setLastBooking(lastBooking);

                return resultItem;
            }
            return resultItem;
        } else throw new NotFoundException("No such item with ID: " + itemId);

    }

    @Override
    public List<ItemForOwnerDto> getAllById(Long userId) {

        List<ItemForOwnerDto> list = storage.findByOwnerId(userId).stream()
                .map(item -> mapper.map(item, ItemForOwnerDto.class))
                .collect(Collectors.toList());

        List<ItemForOwnerDto> resultList = new ArrayList<>();

        for (ItemForOwnerDto item : list) {
            List<Booking> bookList = bookingStorage.findAllBookingByItemId(item.getId());
            if (bookList.isEmpty()) {
                return list;
            }
            List<Booking> sortedBookList = bookList.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getStart))
                    .collect(Collectors.toList());
            if (!sortedBookList.isEmpty()) {
                NextBookingDto nextBooking = mapper.map(sortedBookList.get(0), NextBookingDto.class);
                LastBookingDto lastBooking = mapper.map(sortedBookList.get(sortedBookList.size() - 1), LastBookingDto.class);
                if (lastBooking.getId().equals(nextBooking.getId()) && lastBooking.getBookerId().equals(nextBooking.getBookerId())) {
                    item.setNextBooking(nextBooking);
                    resultList.add(item);
                } else {
                    item.setNextBooking(nextBooking);
                    item.setLastBooking(lastBooking);
                }
            }
            resultList.add(item);
        }
        return resultList;
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
