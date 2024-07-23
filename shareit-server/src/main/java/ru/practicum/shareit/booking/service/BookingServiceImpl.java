package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.UserBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.validation.BookingValidator;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage storage;
    private final ModelMapper mapper;
    private final BookingValidator validator;

    @Override
    public RequestBookingDto createBooking(CreateBookingDto createDto, Long bookerId) {

        User user = validator.validateBooker(bookerId);
        Item item = validator.validateItem(createDto);

        if (!user.getId().equals(item.getOwnerId())) {

            UserBookingDto userBookingDto = mapper.map(user, UserBookingDto.class);
            ItemBookingDto itemBookingDto = mapper.map(item, ItemBookingDto.class);
            Booking booking = mapper.map(createDto, Booking.class);

            booking.setBooker(user)
                    .setItem(item)
                    .setStatus(Status.WAITING)
                    .setId(null);

            RequestBookingDto requestBookingDto = mapper.map(
                    storage.save(booking),
                    RequestBookingDto.class);

            requestBookingDto.setItem(itemBookingDto);
            requestBookingDto.setBooker(userBookingDto);

            return requestBookingDto;
        } else throw new NotFoundException("Owner can't book his own items");
    }

    @Override
    public RequestBookingDto confirm(Long bookingId, boolean approve, Long ownerId) {
        Booking booking = validator.validateBooking(bookingId);
        if (ownerId.equals(booking.getItem().getOwnerId())) {
            if (approve) {
                if (booking.getStatus().equals(Status.APPROVED)) {
                    throw new ValidationException("Booking already approved");
                } else {
                    booking.setStatus(Status.APPROVED);
                }
            } else {
                booking.setStatus(Status.REJECTED);
            }
        } else {
            throw new NotFoundException("Only owner of item can change status");
        }
        storage.save(booking);

        RequestBookingDto request = mapper.map(booking, RequestBookingDto.class);
        ItemBookingDto item = mapper.map(booking.getItem(), ItemBookingDto.class);
        UserBookingDto booker = mapper.map(booking.getBooker(), UserBookingDto.class);


        return request.setBooker(booker).setItem(item);
    }

    @Override
    public RequestBookingDto getBookingById(Long bookingId, Long userId) {
        validator.validateBooker(userId);
        Booking book = validator.validateBooking(bookingId);
        if (book.getBooker().getId().equals(userId) ||
                book.getItem().getOwnerId().equals(userId)) {
            return mapper.map(book, RequestBookingDto.class);
        } else {
            throw new NotFoundException("User with id " + userId + "haven't access to this booking");
        }
    }

    @Override
    public List<RequestBookingDto> getListUserBookingsByStatus(String state, Long bookerId, int from, int size) {
        validator.validateBooker(bookerId);
        Page<Booking> page = storage.findByBookerId(bookerId,
                PageRequest.of(from / size, size, Sort.Direction.DESC, "start"));
        return resultListByState(state, page.getContent());
    }

    @Override
    public List<RequestBookingDto> getListOwnerBookingsByStatus(String state, Long ownerId, int from, int size) {
        validator.validateBooker(ownerId);
        Page<Booking> page = storage.findByItemOwnerId(ownerId,
                PageRequest.of(from / size, size, Sort.Direction.DESC, "start"));
        return resultListByState(state, page.getContent());
    }

    public List<RequestBookingDto> resultListByState(String state, List<Booking> userBookingsList) {
        List<Booking> resultList;
        switch (state) {
            case "ALL": {
                resultList = userBookingsList;
                break;
            }
            case "CURRENT": {
                resultList = userBookingsList.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) &&
                                booking.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            }
            case "PAST": {
                resultList = storage.findAllByEndIsBefore(LocalDateTime.now());
                break;
            }
            case "FUTURE": {
                resultList = storage.findAllByStartIsAfter(LocalDateTime.now());
                break;
            }
            case "WAITING": {
                resultList = userBookingsList.stream()
                        .filter(booking -> booking.getStatus().equals(Status.WAITING))
                        .collect(Collectors.toList());
                break;
            }
            case "REJECTED": {
                resultList = userBookingsList.stream()
                        .filter(booking -> booking.getStatus().equals(Status.REJECTED))
                        .collect(Collectors.toList());
                break;
            }
            default: {
                throw new InternalServerException("Unknown state: " + state);
            }
        }
        return resultList.stream()
                .map(booking -> mapper.map(booking, RequestBookingDto.class))
                .sorted(Comparator.comparing(RequestBookingDto::getStart).reversed())
                .collect(Collectors.toList());
    }
}
