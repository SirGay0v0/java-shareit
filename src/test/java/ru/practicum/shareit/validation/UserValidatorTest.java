package ru.practicum.shareit.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.validation.UserValidator;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @InjectMocks
    private UserValidator userValidator;

    @Mock
    private UserStorage userStorage;

    private User user;

    @BeforeEach
    void setup() {
        user = new User()
                .setId(1L)
                .setName("Test User")
                .setEmail("testuser@example.com");
    }

    @Test
    public void testValidateByEmail_Success() {
        when(userStorage.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        userValidator.validateByEmail(user);
    }

    @Test
    public void testValidateByEmail_UserExists() {
        when(userStorage.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            userValidator.validateByEmail(user);
        });

        assertThat(exception.getMessage()).isEqualTo("User with email: testuser@example.com already exist");
    }

    @Test
    public void testValidateById_Success() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        User result = userValidator.validateById(user.getId());

        assertThat(result).isEqualTo(user);
    }

    @Test
    public void testValidateById_NotFoundException() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userValidator.validateById(user.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("No such User with id 1");
    }
}
