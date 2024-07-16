package ru.practicum.shareit.IntegrationsTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookingServiceImplIntegrationTest {

    private final BookingService bookingService;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testCreateBooking() {
        User user = new User()
                .setName("Test User")
                .setEmail("testuser@example.com");
        User savedUser = userStorage.save(user);

        User owner = new User()
                .setName("Owner")
                .setEmail("owner@example.com");
        User savedOwner = userStorage.save(owner);

        Item item = new Item()
                .setName("Test Item")
                .setDescription("Test Description")
                .setAvailable(true)
                .setOwnerId(savedOwner.getId());
        Item savedItem = itemStorage.save(item);

        CreateBookingDto createBookingDto = new CreateBookingDto()
                .setItemId(savedItem.getId())
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().plusDays(2));

        RequestBookingDto bookingDto = bookingService.createBooking(createBookingDto, savedUser.getId());

        assertNotNull(bookingDto);
        assertEquals(Status.WAITING, bookingDto.getStatus());
        assertEquals(savedUser.getId(), bookingDto.getBooker().getId());
        assertEquals(savedItem.getId(), bookingDto.getItem().getId());
    }
}
