package ru.practicum.shareit.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.requests.dto.CreateRequestDto;
import ru.practicum.shareit.requests.model.Request;
import ru.practicum.shareit.requests.storage.RequestStorage;
import ru.practicum.shareit.requests.validation.RequestValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestValidatorTest {

    @InjectMocks
    private RequestValidator requestValidator;

    @Mock
    private UserStorage userStorage;

    @Mock
    private RequestStorage requestStorage;

    private User user;
    private CreateRequestDto createRequestDto;

    @BeforeEach
    void setup() {
        user = new User()
                .setId(1L)
                .setName("Test User")
                .setEmail("testuser@example.com");

        createRequestDto = new CreateRequestDto()
                .setDescription("Test Description");
    }

    @Test
    public void testCreateValidate_Success() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        requestValidator.createValidate(user.getId(), createRequestDto);
    }

    @Test
    public void testCreateValidate_UserNotFound() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            requestValidator.createValidate(user.getId(), createRequestDto);
        });

        assertThat(exception.getMessage()).isEqualTo("No such user with ID: 1");
    }

    @Test
    public void testCreateValidate_BlankDescription() {
        createRequestDto.setDescription("");
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            requestValidator.createValidate(user.getId(), createRequestDto);
        });

        assertThat(exception.getMessage()).isEqualTo("Incorrect field description");
    }

    @Test
    public void testValidateById_Success() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        requestValidator.validateById(user.getId());
    }

    @Test
    public void testValidateById_NotFoundException() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            requestValidator.validateById(user.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("No such user with ID: 1");
    }

    @Test
    public void testValidatePage_Success() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        requestValidator.validatePage(user.getId(), PageRequest.of(0, 10));
    }

    @Test
    public void testValidateRequestAndId_Success() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestStorage.findById(anyLong())).thenReturn(Optional.of(new Request()));

        requestValidator.validateRequestAndId(1L, user.getId());
    }

    @Test
    public void testValidateRequestAndId_UserNotFound() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            requestValidator.validateRequestAndId(1L, user.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("No such user with ID: 1");
    }

    @Test
    public void testValidateRequestAndId_RequestNotFound() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestStorage.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            requestValidator.validateRequestAndId(1L, user.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("No such request with ID: 1");
    }
}
