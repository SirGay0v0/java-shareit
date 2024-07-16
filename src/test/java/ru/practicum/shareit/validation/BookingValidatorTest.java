package ru.practicum.shareit.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.validation.BookingValidator;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingValidatorTest {

    @InjectMocks
    private BookingValidator bookingValidator;

    @Mock
    private UserStorage userStorage;

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private BookingStorage bookingStorage;

    private User user;
    private Item item;
    private CreateBookingDto createBookingDto;
    private Booking booking;

    @BeforeEach
    void setup() {
        user = new User()
                .setId(1L)
                .setName("Test User")
                .setEmail("testuser@example.com");

        item = new Item()
                .setId(1L)
                .setName("Test Item")
                .setDescription("Test Description")
                .setAvailable(true)
                .setOwnerId(2L);

        createBookingDto = new CreateBookingDto()
                .setItemId(1L)
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().plusDays(2));

        booking = new Booking()
                .setId(1L)
                .setBooker(user)
                .setItem(item)
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    public void testValidateItem_Success() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        Item validatedItem = bookingValidator.validateItem(createBookingDto);

        assertThat(validatedItem).isEqualTo(item);
    }

    @Test
    public void testValidateItem_NotFoundException() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingValidator.validateItem(createBookingDto);
        });

        assertThat(exception.getMessage()).isEqualTo("No such item with ID: 1");
    }

    @Test
    public void testValidateItem_Unavailable() {
        item.setAvailable(false);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingValidator.validateItem(createBookingDto);
        });

        assertThat(exception.getMessage()).isEqualTo("This item is unavailable to book");
    }

    @Test
    public void testValidateItem_InvalidDates() {
        createBookingDto.setEnd(LocalDateTime.now().minusDays(1));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingValidator.validateItem(createBookingDto);
        });

        assertThat(exception.getMessage()).isEqualTo("Invalid field value end or start");
    }

    @Test
    public void testValidateBooker_Success() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        User validatedUser = bookingValidator.validateBooker(user.getId());

        assertThat(validatedUser).isEqualTo(user);
    }

    @Test
    public void testValidateBooker_NotFoundException() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingValidator.validateBooker(user.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("No such user with ID: 1");
    }

    @Test
    public void testValidateBooking_Success() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking validatedBooking = bookingValidator.validateBooking(booking.getId());

        assertThat(validatedBooking).isEqualTo(booking);
    }

    @Test
    public void testValidateBooking_NotFoundException() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingValidator.validateBooking(booking.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("No such booking with ID: 1");
    }

    @Test
    public void testValidatePage_Success() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        bookingValidator.validatePage(0, 10, user.getId());
    }

    @Test
    public void testValidatePage_ValidationException() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingValidator.validatePage(-1, 10, user.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("Params from or size has wrong value!");
    }
}
