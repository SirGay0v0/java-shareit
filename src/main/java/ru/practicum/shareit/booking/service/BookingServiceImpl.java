package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage storage;

    @Override
    public Booking createBooking(Booking booking) {
        storage.save(booking);
        return booking;
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
        LocalDate localDateNow = LocalDate.now();
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
