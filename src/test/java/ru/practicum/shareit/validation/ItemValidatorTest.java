package ru.practicum.shareit.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comments.dto.CreateCommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.validation.ItemValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemValidatorTest {

    @InjectMocks
    private ItemValidator itemValidator;

    @Mock
    private UserStorage userStorage;

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private BookingStorage bookingStorage;

    private User user;
    private Item item;
    private Booking booking;
    private CreateCommentDto comment;

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

        booking = new Booking()
                .setId(1L)
                .setBooker(user)
                .setItem(item)
                .setStart(LocalDateTime.now().minusDays(2))
                .setEnd(LocalDateTime.now().minusDays(1))
                .setStatus(Status.APPROVED);

        comment = new CreateCommentDto()
                .setText("This is a comment");
    }

    @Test
    public void testValidateByExistingUser_Success() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        itemValidator.validateByExistingUser(user.getId());
    }

    @Test
    public void testValidateByExistingUser_NotFoundException() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemValidator.validateByExistingUser(user.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("No such user with ID: 1");
    }

    @Test
    public void testValidateItem_Success() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        Item validatedItem = itemValidator.validateItem(item.getId());

        assertThat(validatedItem).isEqualTo(item);
    }

    @Test
    public void testValidateItem_NotFoundException() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemValidator.validateItem(item.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("No such item with ID: 1");
    }

    @Test
    public void testValidateComment_Success() {
        when(bookingStorage.findByItemId(anyLong())).thenReturn(List.of(booking));

        itemValidator.validateComment(comment, user.getId(), item.getId());
    }

    @Test
    public void testValidateComment_UserDidNotBookItem() {
        when(bookingStorage.findByItemId(anyLong())).thenReturn(List.of());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            itemValidator.validateComment(comment, user.getId(), item.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("This user didn't book this item anytime");
    }

    @Test
    public void testValidateComment_BlankCommentText() {
        comment.setText("");
        when(bookingStorage.findByItemId(anyLong())).thenReturn(List.of(booking));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            itemValidator.validateComment(comment, user.getId(), item.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("The comment must contains some text");
    }
}
