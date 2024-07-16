package ru.practicum.shareit.storageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.requests.model.Request;
import ru.practicum.shareit.requests.storage.RequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ComponentScan(basePackages = "ru.practicum.shareit")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestStorageTest {

    private final RequestStorage requestStorage;
    private final UserStorage userStorage;

    @Test
    public void testFindAllByAuthorIs() {
        User author = new User()
                .setName("Author")
                .setEmail("author@example.com");
        userStorage.save(author);

        Request request = new Request()
                .setAuthor(author.getId())
                .setDescription("Test Request")
                .setCreated(LocalDateTime.now());
        requestStorage.save(request);

        List<Request> foundRequests = requestStorage.findAllByAuthorIs(author.getId());

        assertThat(foundRequests).isNotEmpty();
        assertThat(foundRequests).contains(request);
    }

    @Test
    public void testFindAllByAuthorNotInOrderByCreatedDesc() {
        User author1 = new User()
                .setName("Author1")
                .setEmail("author1@example.com");
        userStorage.save(author1);

        User author2 = new User()
                .setName("Author2")
                .setEmail("author2@example.com");
        userStorage.save(author2);

        Request request1 = new Request()
                .setAuthor(author1.getId())
                .setDescription("Test Request 1")
                .setCreated(LocalDateTime.now());
        requestStorage.save(request1);

        Request request2 = new Request()
                .setAuthor(author2.getId())
                .setDescription("Test Request 2")
                .setCreated(LocalDateTime.now().plusDays(1));
        requestStorage.save(request2);

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Request> foundRequests = requestStorage
                .findAllByAuthorNotInOrderByCreatedDesc(List.of(author1.getId()), pageRequest).getContent();

        assertThat(foundRequests).isNotEmpty();
        assertThat(foundRequests).contains(request2);
        assertThat(foundRequests).doesNotContain(request1);
    }

    @Test
    public void testFindAllById() {
        User author = new User()
                .setName("Author")
                .setEmail("author@example.com");
        userStorage.save(author);

        Request request = new Request()
                .setAuthor(author.getId())
                .setDescription("Test Request")
                .setCreated(LocalDateTime.now());
        requestStorage.save(request);

        Request foundRequest = requestStorage.findAllById(request.getId());

        assertThat(foundRequest).isNotNull();
        assertThat(foundRequest).isEqualTo(request);
    }
}
