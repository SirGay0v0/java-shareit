package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.validation.BookingValidator;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage storage;
    private final ModelMapper mapper;
    private final BookingValidator validator;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public RequestBookingDto createBooking(CreateBookingDto createDto, Long bookerId) {

        User user = validator.validateBooker(bookerId);
        Item item = validator.validateItem(createDto);

        if (!user.getId().equals(item.getOwnerId())) {

            UserBookingDto userBookingDto = mapper.map(
                    user,
                    UserBookingDto.class);
            ItemBookingDto itemBookingDto = mapper.map(
                    item,
                    ItemBookingDto.class);

            Booking booking = mapper.map(createDto, Booking.class);
            booking.setBooker(user)
                    .setItem(item)
                    .setStatus(Status.WAITING)
                    .setId(null);

            RequestBookingDto requestBookingDto = mapper.map(storage.save(booking), RequestBookingDto.class);
            requestBookingDto.setItem(itemBookingDto)
                    .setBooker(userBookingDto);
            return requestBookingDto;
        } else throw new NotFoundException("Owner can not be booker");
    }

    @Override
    public RequestBookingDto confirm(Long bookingId, boolean approve, Long ownerId) {
        Optional<Booking> booking = storage.findById(bookingId);
        if (booking.isPresent()) {
            if (ownerId.equals(booking.get().getItem().getOwnerId())) {
                if (approve) {
                    if (booking.get().getStatus().equals(Status.APPROVED)) {
                        throw new ValidationException("Booking already approved");
                    } else {
                        booking.get().setStatus(Status.APPROVED);
                    }
                } else {
                    booking.get().setStatus(Status.REJECTED);
                }
            } else {
                throw new NotFoundException("Only owner of item can change status");
            }
            storage.save(booking.get());
            RequestBookingDto request = mapper.map(booking.get(), RequestBookingDto.class);
            ItemBookingDto item = mapper.map(booking.get().getItem(), ItemBookingDto.class);
            UserBookingDto booker = new UserBookingDto();
            booker.setId(booking.get().getBooker().getId());
            request.setBooker(booker).setItem(item);
            return request;
        } else throw new NotFoundException("No such item with id: " + bookingId);
    }

    @Override
    public RequestBookingDto getBookingById(Long bookingId, Long userId) {
        Optional<Booking> bookingOpt = storage.findById(bookingId);
        Optional<User> userOpt = userStorage.findById(userId);
        if (bookingOpt.isPresent()) {
            if (userOpt.isPresent() &&
                    (bookingOpt.get().getBooker().getId().equals(userId) ||
                            bookingOpt.get().getItem().getOwnerId().equals(userId))) {
                return mapper.map(bookingOpt.get(), RequestBookingDto.class);
            } else {
                throw new NotFoundException("User with id " + userId + "haven't access to this booking");
            }
        } else {
            throw new NotFoundException("No such item with id: " + bookingId);
        }
    }

    @Override
    public List<RequestBookingDto> getListUserBookingsByStatus(String state, Long bookerId) {
        LocalDateTime localDateNow = LocalDateTime.now();
        if (!userStorage.existsById(bookerId)) {
            throw new NotFoundException("No such user with id " + bookerId);
        }
        List<Booking> resultList = storage.findByBookerId(bookerId);
        switch (state) {
            case "ALL": {
                return resultList.stream()
                        .map(booking -> mapper.map(booking, RequestBookingDto.class))
                        .sorted(Comparator.comparing(RequestBookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case "CURRENT": {
                return resultList.stream()
                        .filter(booking -> booking.getStart().isBefore(localDateNow) &&
                                booking.getEnd().isAfter(localDateNow))
                        .map(booking -> mapper.map(booking, RequestBookingDto.class))
                        .sorted(Comparator.comparing(RequestBookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case "**PAST**": {
                return resultList.stream()
                        .filter(booking -> booking.getEnd().isBefore(localDateNow))
                        .map(booking -> mapper.map(booking, RequestBookingDto.class))
                        .sorted(Comparator.comparing(RequestBookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case "FUTURE": {
                return resultList.stream()
                        .filter(booking -> booking.getStart().isAfter(localDateNow))
                        .map(booking -> mapper.map(booking, RequestBookingDto.class))
                        .sorted(Comparator.comparing(RequestBookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case "WAITING": {
                return resultList.stream()
                        .filter(booking -> booking.getStatus().equals(Status.WAITING))
                        .map(booking -> mapper.map(booking, RequestBookingDto.class))
                        .sorted(Comparator.comparing(RequestBookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case "REJECTED": {
                return resultList.stream()
                        .filter(booking -> booking.getStatus().equals(Status.REJECTED))
                        .map(booking -> mapper.map(booking, RequestBookingDto.class))
                        .sorted(Comparator.comparing(RequestBookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            }
            default: {
                throw new InternalServerException("Unknown state: " + state);
            }
        }
    }

    @Override
    public List<RequestBookingDto> getListOwnerBookingsByStatus(String state, Long ownerId) {
        LocalDateTime localDateNow = LocalDateTime.now();
        if (!userStorage.existsById(ownerId)) {
            throw new NotFoundException("No such user with id " + ownerId);
        }
        List<Booking> resultList = storage.findAllBookingsByItemOwnerId(ownerId);

        switch (state) {
            case "ALL": {
                return resultList.stream()
                        .map(booking -> mapper.map(booking, RequestBookingDto.class))
                        .sorted(Comparator.comparing(RequestBookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case "CURRENT": {
                return resultList.stream()
                        .filter(booking -> booking.getStart().isBefore(localDateNow) &&
                                booking.getEnd().isAfter(localDateNow))
                        .map(booking -> mapper.map(booking, RequestBookingDto.class))
                        .sorted(Comparator.comparing(RequestBookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case "**PAST**": {
                return resultList.stream()
                        .filter(booking -> booking.getEnd().isBefore(localDateNow))
                        .map(booking -> mapper.map(booking, RequestBookingDto.class))
                        .sorted(Comparator.comparing(RequestBookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case "FUTURE": {
                return resultList.stream()
                        .filter(booking -> booking.getStart().isAfter(localDateNow))
                        .map(booking -> mapper.map(booking, RequestBookingDto.class))
                        .sorted(Comparator.comparing(RequestBookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case "WAITING": {
                return resultList.stream()
                        .filter(booking -> booking.getStatus().equals(Status.WAITING))
                        .map(booking -> mapper.map(booking, RequestBookingDto.class))
                        .sorted(Comparator.comparing(RequestBookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case "REJECTED": {
                return resultList.stream()
                        .filter(booking -> booking.getStatus().equals(Status.REJECTED))
                        .map(booking -> mapper.map(booking, RequestBookingDto.class))
                        .sorted(Comparator.comparing(RequestBookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            }
            default: {
                throw new InternalServerException("Unknown state: " + state);
            }
        }
    }
}
