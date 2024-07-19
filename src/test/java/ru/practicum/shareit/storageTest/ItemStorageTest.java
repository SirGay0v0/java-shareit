package ru.practicum.shareit.storageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
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
public class ItemStorageTest {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final RequestStorage requestStorage;

    @Test
    public void testFindByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCaseAndAvailableIsTrue() {
        User owner = new User()
                .setName("Owner")
                .setEmail("owner@example.com");
        userStorage.save(owner);

        Item item = new Item()
                .setName("TestItem")
                .setDescription("TestDescription")
                .setAvailable(true)
                .setOwnerId(owner.getId());
        itemStorage.save(item);

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Item> foundItems = itemStorage
                .findItemsWhichContainsText(
                        "test",
                        "test",
                        pageRequest).getContent();

        assertThat(foundItems).isNotEmpty();
        assertThat(foundItems).contains(item);
    }

    @Test
    public void testFindByOwnerId() {
        User owner = new User()
                .setName("Owner")
                .setEmail("owner@example.com");
        userStorage.save(owner);

        Item item1 = new Item()
                .setName("Item1")
                .setDescription("Description1")
                .setAvailable(true)
                .setOwnerId(owner.getId());

        Item item2 = new Item()
                .setName("Item2")
                .setDescription("Description2")
                .setAvailable(true)
                .setOwnerId(owner.getId());

        itemStorage.save(item1);
        itemStorage.save(item2);

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Item> foundItems = itemStorage.findByOwnerId(owner.getId(), pageRequest).getContent();

        assertThat(foundItems).isNotEmpty();
        assertThat(foundItems).contains(item1, item2);
    }

    @Test
    public void testFindAllByRequestId() {
        User owner = new User()
                .setName("Owner")
                .setEmail("owner@example.com");
        userStorage.save(owner);

        Request request = new Request()
                .setAuthor(owner.getId())
                .setDescription("Test Request")
                .setCreated(LocalDateTime.now());
        requestStorage.save(request);

        Item item = new Item()
                .setName("TestItem")
                .setDescription("TestDescription")
                .setAvailable(true)
                .setOwnerId(owner.getId())
                .setRequestId(request.getId());
        itemStorage.save(item);

        List<Item> foundItems = itemStorage.findAllByRequestId(request.getId());

        assertThat(foundItems).isNotEmpty();
        assertThat(foundItems).contains(item);
    }
}
