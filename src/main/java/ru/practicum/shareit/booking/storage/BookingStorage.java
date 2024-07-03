package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long userId);

    @Query(value = "SELECT b FROM Booking b JOIN b.item i WHERE i.ownerId = ?1")
    List<Booking> findAllBookingsByItemOwnerId(Long ownerId);


    @Query(value = "select b from Booking b join b.item i where i.id = ?1")
    List<Booking> findAllBookingByItemId(Long itemId);
}
