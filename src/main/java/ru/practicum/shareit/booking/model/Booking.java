package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "start_time")
    LocalDate start;
    @Column(name = "end_time")
    LocalDate end;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    Status status;
    @Column(name = "booker_id")
    Long bookerId;
    @Column(name = "item_id")
    Long itemId;
}
