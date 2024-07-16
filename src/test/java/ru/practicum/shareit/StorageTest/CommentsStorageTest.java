package ru.practicum.shareit.StorageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.comments.storage.CommentsStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ComponentScan(basePackages = "ru.practicum.shareit")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentsStorageTest {

    private final CommentsStorage commentsStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Test
    public void testFindByItemId() {
        User user = new User()
                .setName("Test User")
                .setEmail("testuser@example.com");
        userStorage.save(user);

        Item item = new Item()
                .setName("Test Item")
                .setDescription("Test Description")
                .setAvailable(true)
                .setOwnerId(user.getId());
        itemStorage.save(item);

        Comment comment = new Comment()
                .setText("Great Item!")
                .setAuthor(user)
                .setItem(item)
                .setCreated(LocalDateTime.now());
        commentsStorage.save(comment);

        List<Comment> foundComments = commentsStorage.findByItemId(item.getId());

        assertThat(foundComments).isNotEmpty();
        assertThat(foundComments).contains(comment);
    }
}
