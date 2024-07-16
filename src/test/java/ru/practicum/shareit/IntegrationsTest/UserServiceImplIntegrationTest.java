package ru.practicum.shareit.IntegrationsTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceImplIntegrationTest {

    private final UserService userService;

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void testUpdate() {

        User user = new User()
                .setName("test name")
                .setEmail("test@example.com");

        User updateUser = new User()
                .setName("update test name")
                .setEmail("update@example.com");

        user = userService.create(user);
        User result = userService.update(user.getId(), updateUser);

        assertNotNull(result);
        assertEquals(result.getId(), user.getId());
        assertEquals(result.getName(), updateUser.getName());
        assertEquals(result.getEmail(), updateUser.getEmail());
    }
}
