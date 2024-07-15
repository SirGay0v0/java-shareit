package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerIdOrderByStartDesc(Long userId, PageRequest pageRequest);

    Page<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId, PageRequest pageRequest);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findAllByEndIsBefore(LocalDateTime before);

    List<Booking> findAllByStartIsAfter(LocalDateTime after);

    List<Booking> findByItemIdAndStatus(Long itemId, Status status);

    Booking findFirstByItemIdAndStatusAndStartIsAfterOrderByStart(Long itemId, Status status, LocalDateTime now);

    Booking findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(Long itemId, Status status, LocalDateTime now);


}
