package ru.practicum.shareit.IntegrationsTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceImplIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testGetItemById() {

        User user = new User()
                .setEmail("test@example.com")
                .setName("Test user");

        ItemRequestDto testRequestItemDto = new ItemRequestDto()
                .setAvailable(true)
                .setRequestId(null)
                .setName("Test item")
                .setDescription("Test description");

        User resultUser = userService.create(user);
        Item resultItem = itemService.addNewItem(resultUser.getId(), testRequestItemDto);

        ItemForOwnerDto checkItem = itemService.getItemById(resultItem.getId(), resultUser.getId());

        assertNotNull(checkItem);
        assertEquals(checkItem.getId(), resultItem.getId());
        assertEquals(checkItem.getName(), resultItem.getName());
        assertEquals(checkItem.getDescription(), resultItem.getDescription());
        assertEquals(checkItem.getAvailable(), resultItem.getAvailable());
    }
}