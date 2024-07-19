package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerId(Long userId, PageRequest pageRequest);

    Page<Booking> findByItemOwnerId(Long ownerId, PageRequest pageRequest);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findAllByEndIsBefore(LocalDateTime before);

    List<Booking> findAllByStartIsAfter(LocalDateTime after);

    List<Booking> findByItemIdAndStatus(Long itemId, Status status);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.id = ?1 AND b.status = ?2 AND b.start > ?3 " +
            "ORDER BY b.start ASC")
    Page<Booking> findBookingByIdStatusStartAfter(Long itemId, Status status, LocalDateTime now, PageRequest pageRequest);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.id = ?1 AND b.status = ?2 AND b.start < ?3 " +
            "ORDER BY b.end DESC")
    Page<Booking> findBookingByIdStatusStartBefore(Long itemId, Status status, LocalDateTime now, PageRequest pageRequest);
}
