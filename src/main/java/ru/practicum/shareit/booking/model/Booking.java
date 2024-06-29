package ru.practicum.shareit.booking.model;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "start_time")
    LocalDateTime start;
    @Column(name = "end_time")
    LocalDateTime end;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    Status status;
    @OneToOne(fetch = FetchType.LAZY)
    User booker;
    @OneToOne(fetch = FetchType.LAZY)
    Item item;
}
