package ru.practicum.shareit.integrationsTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.requests.dto.CreateRequestDto;
import ru.practicum.shareit.requests.dto.RequestForUserDto;
import ru.practicum.shareit.requests.model.Request;
import ru.practicum.shareit.requests.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class RequestServiceImplIntegrationTest {

    private final RequestService requestService;
    private final UserService userService;

    @Test
    public void testGetAllFromOtherUsers() {
        User author = new User()
                .setName("Author")
                .setEmail("author@example.com");
        User savedAuthor = userService.create(author);

        User otherUser = new User()
                .setName("Other User")
                .setEmail("otheruser@example.com");
        User savedOtherUser = userService.create(otherUser);

        CreateRequestDto createRequestDto = new CreateRequestDto();
        createRequestDto.setDescription("Test Request Description");

        Request savedRequest = requestService.create(createRequestDto, savedAuthor.getId());

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RequestForUserDto> requests = requestService.getAllFromOtherUsers(pageRequest, savedOtherUser.getId());

        assertNotNull(requests);
        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
        assertEquals(savedRequest.getDescription(), requests.get(0).getDescription());
    }
}
