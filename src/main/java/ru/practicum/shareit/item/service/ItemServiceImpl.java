package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.comments.dto.CreateCommentDto;
import ru.practicum.shareit.item.comments.dto.RequestCommentDto;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.comments.storage.CommentsStorage;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.LastBookingDto;
import ru.practicum.shareit.item.dto.NextBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.validation.ItemValidator;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage storage;
    private final ModelMapper mapper;
    private final ItemValidator validator;
    private final BookingStorage bookingStorage;
    private final CommentsStorage commentsStorage;
    private final UserStorage userStorage;

    @Override
    public Item addNewItem(Long ownerId, ItemRequestDto itemRequestDto) {
        validator.validateByExistingUser(ownerId);
        Item item = mapper.map(itemRequestDto, Item.class);
        item.setOwnerId(ownerId);
        return storage.save(item);
    }

    @Override
    public Item updateItem(Long ownerId, Long itemId, ItemRequestDto itemRequestDto) throws AccessDeniedException {
        Item item = validator.validateItem(itemId);
        if (item.getOwnerId().equals(ownerId)) {
            if (itemRequestDto.getName() != null) {
                item.setName(itemRequestDto.getName());
            }
            if (itemRequestDto.getDescription() != null) {
                item.setDescription(itemRequestDto.getDescription());
            }
            if (itemRequestDto.getAvailable() != null) {
                item.setAvailable(itemRequestDto.getAvailable());
            }
        } else {
            throw new AccessDeniedException("User with ID " + ownerId + " don't have access to item with ID: " + itemId);
        }
        return storage.save(item);
    }

    @Override
    public ItemForOwnerDto getItemById(Long itemId, Long userId) {
        Item item = validator.validateItem(itemId);

            ItemForOwnerDto resultItem = mapper.map(item, ItemForOwnerDto.class);
            List<Booking> bookList = bookingStorage.findAllBookingByItemId(item.getId());
            if (!bookList.isEmpty() && item.getOwnerId().equals(userId)) {

                NextBookingDto next = mapper.map(
                        bookList.stream()
                                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                                .filter(booking -> booking.getStatus().equals(Status.APPROVED))
                                .min(Comparator.comparing(Booking::getStart)),
                        NextBookingDto.class);

                LastBookingDto last = mapper.map(
                        bookList.stream()
                                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                                .filter(booking -> booking.getStatus().equals(Status.APPROVED))
                                .max(Comparator.comparing(Booking::getEnd)),
                        LastBookingDto.class);

                if (last != null && next != null && last.getId().equals(next.getId())
                        && last.getBookerId().equals(next.getBookerId())) {
                    resultItem.setNextBooking(next);
                } else {
                    resultItem.setNextBooking(next);
                    resultItem.setLastBooking(last);
                }
            }

            List<Comment> commentsList = commentsStorage.findByItemContaining(resultItem.getId());
            if (commentsList.isEmpty()) {
                resultItem.setComments(Collections.emptyList());
            } else {
                List<RequestCommentDto> requestComments = commentsList.stream()
                        .map(comment -> mapper.map(comment, RequestCommentDto.class))
                        .collect(Collectors.toList());
                resultItem.setComments(requestComments);
            }
            return resultItem;
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
            } else {
                NextBookingDto next = mapper.map(
                        bookList.stream()
                                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                                .filter(booking -> booking.getStatus().equals(Status.APPROVED))
                                .min(Comparator.comparing(Booking::getStart)),
                        NextBookingDto.class);
                LastBookingDto last = mapper.map(
                        bookList.stream()
                                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                                .filter(booking -> booking.getStatus().equals(Status.APPROVED))
                                .min(Comparator.comparing(Booking::getEnd)),
                        LastBookingDto.class);

                if (last.getId().equals(next.getId()) && last.getBookerId().equals(next.getBookerId())) {
                    item.setNextBooking(next);
                    resultList.add(item);
                } else {
                    item.setNextBooking(next);
                    item.setLastBooking(last);
                }
            }

            List<Comment> commentsList = commentsStorage.findByItemContaining(item.getId());
            List<RequestCommentDto> requestComments = commentsList.stream()
                    .map(comment -> mapper.map(comment, RequestCommentDto.class))
                    .collect(Collectors.toList());
            if (commentsList.isEmpty()) {
                item.setComments(Collections.emptyList());
            } else {
                item.setComments(requestComments);
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

    @Override
    public RequestCommentDto addComment(CreateCommentDto create, Long userId, Long itemId) {
        validator.validateComment(create, userId, itemId);
        Comment newComment = mapper.map(create, Comment.class);
        newComment.setItem(storage.findById(itemId).get());
        newComment.setCreated(LocalDateTime.now());
        newComment.setAuthor(userStorage.findById(userId).get());
        return mapper.map(commentsStorage.save(newComment), RequestCommentDto.class);
    }
}
