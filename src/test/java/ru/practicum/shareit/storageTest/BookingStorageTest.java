package ru.practicum.shareit.storageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ComponentScan(basePackages = "ru.practicum.shareit")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingStorageTest {

    private final BookingStorage bookingStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Test
    public void testFindByBookerIdOrderByStartDesc() {
        User user = new User()
                .setName("Test User")
                .setEmail("testuser@example.com");
        userStorage.save(user);

        Item item = new Item()
                .setName("Test Item")
                .setDescription("Test Description")
                .setAvailable(true)
                .setOwnerId(user.getId());
        itemStorage.save(item);

        Booking booking = new Booking()
                .setBooker(user)
                .setItem(item)
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().plusDays(2))
                .setStatus(Status.WAITING);
        bookingStorage.save(booking);

        Page<Booking> bookings = bookingStorage.findByBookerId(
                user.getId(), PageRequest.of(0, 10));

        assertThat(bookings.getContent()).isNotEmpty();
        assertThat(bookings.getContent()).contains(booking);
    }

    @Test
    public void testFindByItemOwnerIdOrderByStartDesc() {
        User user = new User()
                .setName("Test User")
                .setEmail("testuser@example.com");
        userStorage.save(user);

        Item item = new Item()
                .setName("Test Item")
                .setDescription("Test Description")
                .setAvailable(true)
                .setOwnerId(user.getId());
        itemStorage.save(item);

        Booking booking = new Booking()
                .setBooker(user)
                .setItem(item)
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().plusDays(2))
                .setStatus(Status.WAITING);
        bookingStorage.save(booking);

        Page<Booking> bookings = bookingStorage.findByItemOwnerId(
                user.getId(), PageRequest.of(0, 10));

        assertThat(bookings.getContent()).isNotEmpty();
        assertThat(bookings.getContent()).contains(booking);
    }

    @Test
    public void testFindByItemId() {
        User user = new User()
                .setName("Test User")
                .setEmail("testuser@example.com");
        userStorage.save(user);

        Item item = new Item()
                .setName("Test Item")
                .setDescription("Test Description")
                .setAvailable(true)
                .setOwnerId(user.getId());
        itemStorage.save(item);

        Booking booking = new Booking()
                .setBooker(user)
                .setItem(item)
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().plusDays(2))
                .setStatus(Status.WAITING);
        bookingStorage.save(booking);

        List<Booking> bookings = bookingStorage.findByItemId(item.getId());

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).contains(booking);
    }

    @Test
    public void testFindAllByEndIsBefore() {
        User user = new User()
                .setName("Test User")
                .setEmail("testuser@example.com");
        userStorage.save(user);

        Item item = new Item()
                .setName("Test Item")
                .setDescription("Test Description")
                .setAvailable(true)
                .setOwnerId(user.getId());
        itemStorage.save(item);

        Booking booking = new Booking()
                .setBooker(user)
                .setItem(item)
                .setStart(LocalDateTime.now().minusDays(2))
                .setEnd(LocalDateTime.now().minusDays(1))
                .setStatus(Status.WAITING);
        bookingStorage.save(booking);

        List<Booking> bookings = bookingStorage.findAllByEndIsBefore(LocalDateTime.now());

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).contains(booking);
    }

    @Test
    public void testFindAllByStartIsAfter() {
        User user = new User()
                .setName("Test User")
                .setEmail("testuser@example.com");
        userStorage.save(user);

        Item item = new Item()
                .setName("Test Item")
                .setDescription("Test Description")
                .setAvailable(true)
                .setOwnerId(user.getId());
        itemStorage.save(item);

        Booking booking = new Booking()
                .setBooker(user)
                .setItem(item)
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().plusDays(2))
                .setStatus(Status.WAITING);
        bookingStorage.save(booking);

        List<Booking> bookings = bookingStorage.findAllByStartIsAfter(LocalDateTime.now());

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).contains(booking);
    }

    @Test
    public void testFindByItemIdAndStatus() {
        User user = new User()
                .setName("Test User")
                .setEmail("testuser@example.com");
        userStorage.save(user);

        Item item = new Item()
                .setName("Test Item")
                .setDescription("Test Description")
                .setAvailable(true)
                .setOwnerId(user.getId());
        itemStorage.save(item);

        Booking booking = new Booking()
                .setBooker(user)
                .setItem(item)
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().plusDays(2))
                .setStatus(Status.WAITING);
        bookingStorage.save(booking);

        List<Booking> bookings = bookingStorage.findByItemIdAndStatus(item.getId(), Status.WAITING);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).contains(booking);
    }

    @Test
    public void testFindFirstByItemIdAndStatusAndStartIsAfterOrderByStart() {
        User user = new User()
                .setName("Test User")
                .setEmail("testuser@example.com");
        userStorage.save(user);

        Item item = new Item()
                .setName("Test Item")
                .setDescription("Test Description")
                .setAvailable(true)
                .setOwnerId(user.getId());
        itemStorage.save(item);

        Booking booking = new Booking()
                .setBooker(user)
                .setItem(item)
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().plusDays(2))
                .setStatus(Status.WAITING);
        bookingStorage.save(booking);

        Booking foundBooking = bookingStorage.findBookingByIdStatusStartAfter(
                item.getId(),
                Status.WAITING,
                LocalDateTime.now(),
                PageRequest.of(0, 10)
        ).getContent().stream().findFirst().get();

        assertThat(foundBooking).isNotNull();
        assertThat(foundBooking).isEqualTo(booking);
    }

    @Test
    public void testFindFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc() {
        User user = new User()
                .setName("Test User")
                .setEmail("testuser@example.com");
        userStorage.save(user);

        Item item = new Item()
                .setName("Test Item")
                .setDescription("Test Description")
                .setAvailable(true)
                .setOwnerId(user.getId());
        itemStorage.save(item);

        Booking booking = new Booking()
                .setBooker(user)
                .setItem(item)
                .setStart(LocalDateTime.now().minusDays(2))
                .setEnd(LocalDateTime.now().minusDays(1))
                .setStatus(Status.WAITING);
        bookingStorage.save(booking);

        Booking foundBooking = bookingStorage.findBookingByIdStatusStartBefore(
                        item.getId(),
                        Status.WAITING,
                        LocalDateTime.now(),
                        PageRequest.of(0, 10))
                .getContent()
                .stream()
                .findFirst().get();

        assertThat(foundBooking).isNotNull();
        assertThat(foundBooking).isEqualTo(booking);
    }
}
