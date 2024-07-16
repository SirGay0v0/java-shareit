package ru.practicum.shareit.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.validation.UserValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserStorage storage;

    @Mock
    private UserValidator validator;

    @InjectMocks
    private UserServiceImpl service;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User()
                .setId(1L)
                .setName("User")
                .setEmail("user@example.com");
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void createUser_shouldCreateUser_whenValidInput() {
        when(storage.save(any(User.class))).thenReturn(user);

        User result = service.create(user);

        assertNotNull(result);
        verify(storage, times(1)).save(any(User.class));
        assertSame(result, user);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateUser_shouldUpdate_whenValidInputName() {
        User newUser = new User()
                .setName("new Name");

        when(validator.validateById(anyLong())).thenReturn(user);
        when(storage.save(any(User.class))).thenReturn(user);


        User updatedUser = service.update(user.getId(), newUser);

        assertNotNull(updatedUser);
        assertEquals(newUser.getName(), updatedUser.getName());
        assertEquals(user.getEmail(), updatedUser.getEmail());

        verify(validator, times(1)).validateById(anyLong());
        verify(storage, times(1)).save(any(User.class));

    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateUser_shouldUpdate_whenValidInputEmail() {
        User newUser = new User()
                .setEmail("newEmail@example.com");

        when(validator.validateById(anyLong())).thenReturn(user);
        doNothing().when(validator).validateByEmail(any(User.class));
        when(storage.save(any(User.class))).thenReturn(user);


        User updatedUser = service.update(user.getId(), newUser);

        assertNotNull(updatedUser);
        assertEquals(user.getName(), updatedUser.getName());
        assertEquals(newUser.getEmail(), updatedUser.getEmail());

        verify(validator, times(1)).validateById(anyLong());
        verify(validator, times(1)).validateByEmail(any(User.class));
        verify(storage, times(1)).save(any(User.class));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateUser_shouldUpdate_whenNoNewData() {
        User newUser = new User();

        when(validator.validateById(anyLong())).thenReturn(user);
        when(storage.save(any(User.class))).thenReturn(user);


        User updatedUser = service.update(user.getId(), newUser);

        assertNotNull(updatedUser);
        assertEquals(user.getName(), updatedUser.getName());
        assertEquals(user.getEmail(), updatedUser.getEmail());

        verify(validator, times(1)).validateById(anyLong());
        verify(storage, times(1)).save(any(User.class));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateUser_shouldThrowException_whenEmailAlreadyExists() {
        User newUser = new User()
                .setEmail("existing@example.com");

        when(validator.validateById(anyLong())).thenReturn(user);
        doThrow(new InternalServerException("User with email: " + newUser.getEmail() + " already exist"))
                .when(validator).validateByEmail(any(User.class));

        InternalServerException exception = assertThrows(InternalServerException.class, () -> service.update(user.getId(), newUser));
        assertEquals("User with email: " + newUser.getEmail() + " already exist", exception.getMessage());

        verify(validator, times(1)).validateById(anyLong());
        verify(validator, times(1)).validateByEmail(any(User.class));
        verify(storage, never()).save(any(User.class));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void update_shouldThrowNotFoundException_whenUserIdNotFound() {
        User newUser = new User();
        Long nonExistingId = 2L;

        when(validator.validateById(anyLong()))
                .thenThrow(new NotFoundException("No such User with id " + nonExistingId));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.update(nonExistingId, newUser));
        assertEquals("No such User with id " + nonExistingId, exception.getMessage());

        verify(validator, times(1)).validateById(anyLong());
        verify(storage, never()).save(any(User.class));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void delete_shouldDelete_whenValidId() {

        service.delete(user.getId());

        verify(storage, times(1)).deleteById(user.getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getById_shouldReturnUser_whenValidId() {
        when(validator.validateById(anyLong())).thenReturn(user);
        when(storage.getReferenceById(user.getId())).thenReturn(user);

        User result = service.getById(user.getId());

        assertEquals(result, user);

        verify(storage, times(1)).getReferenceById(user.getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getById_shouldThrowNotFoundException_whenInvalidId() {
        Long nonExistingId = 2L;

        when(validator.validateById(anyLong()))
                .thenThrow(new NotFoundException("No such User with id " + nonExistingId));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getById(nonExistingId));
        assertEquals("No such User with id " + nonExistingId, exception.getMessage());

        verify(validator, times(1)).validateById(anyLong());
        verify(storage, never()).getReferenceById(anyLong());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getAll_shouldReturnAllUsers() {
        User user2 = new User()
                .setId(2L)
                .setName("User2")
                .setEmail("user2@example.com");

        List<User> correctUserList = List.of(user, user2);

        when(storage.findAll()).thenReturn(correctUserList);

        List<User> result = service.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(user));
        assertTrue(result.contains(user2));

        verify(storage, times(1)).findAll();
    }
}
