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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Collections;
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
//        validator.validate(createDto.getItemId(), createDto, bookerId);
//        Item item = itemStorage.findById(createDto.getItemId()).get();
//        User user = userStorage.findById(bookerId).get();
        Booking booking = mapper.map(createDto, Booking.class);
//        booking.setUser(user);
//        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        storage.save(booking);
        return mapper.map(booking, RequestBookingDto.class);
    }

    @Override
    public Booking confirm(Long bookingId, boolean approve) {
        Optional<Booking> booking = storage.findById(bookingId);
        if (booking.isPresent()) {
            if (approve) {
                booking.get().setStatus(Status.APPROVED);
            } else {
                booking.get().setStatus(Status.REJECTED);
            }
            storage.save(booking.get());
            return booking.get();
        } else throw new NotFoundException("No such item with id: " + bookingId);
    }

    @Override
    public Booking getBookingById(Long bookingId) {
        Optional<Booking> opt = storage.findById(bookingId);
        if (opt.isPresent()) {
            return opt.get();
        } else {
            throw new NotFoundException("No such item with id: " + bookingId);
        }
    }

    @Override
    public List<Booking> getListBookingsByStatus(String state, Long bookerId) {
        LocalDateTime localDateNow = LocalDateTime.now();
        List<Booking> resultList = storage.findByBookerId(bookerId);
        switch (state) {
            case "ALL": {
                return resultList.stream()
                        .sorted(Comparator.comparing(Booking::getStart))
                        .collect(Collectors.toList());
            }
            case "CURRENT": {
                return resultList.stream()
                        .filter(booking -> booking.getStart().isBefore(localDateNow) &&
                                booking.getEnd().isBefore(localDateNow))
                        .sorted(Comparator.comparing(Booking::getStart))
                        .collect(Collectors.toList());
            }
            case "**PAST**": {
                return resultList.stream()
                        .filter(booking -> booking.getEnd().isBefore(localDateNow))
                        .sorted(Comparator.comparing(Booking::getStart))
                        .collect(Collectors.toList());
            }
            case "FUTURE": {
                return resultList.stream()
                        .filter(booking -> booking.getStart().isBefore(localDateNow))
                        .sorted(Comparator.comparing(Booking::getStart))
                        .collect(Collectors.toList());
            }
            case "WAITING": {
                return resultList.stream()
                        .filter(booking -> booking.getStatus().equals(Status.WAITING))
                        .sorted(Comparator.comparing(Booking::getStart))
                        .collect(Collectors.toList());
            }
            case "REJECTED": {
                return resultList.stream()
                        .filter(booking -> booking.getStatus().equals(Status.REJECTED))
                        .sorted(Comparator.comparing(Booking::getStart))
                        .collect(Collectors.toList());
            }
            default: {
                return Collections.emptyList();
            }
        }
    }
}
