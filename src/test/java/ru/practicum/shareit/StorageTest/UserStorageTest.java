package ru.practicum.shareit.StorageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ComponentScan(basePackages = "ru.practicum.shareit")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTest {

    private final UserStorage userStorage;

    @Test
    public void testFindByEmail() {
        User user = new User()
                .setName("Test User")
                .setEmail("testuser@example.com");
        userStorage.save(user);

        Optional<User> foundUser = userStorage.findByEmail("testuser@example.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(user);
    }

    @Test
    public void testSaveAndRetrieveUser() {
        User user = new User()
                .setName("Test User")
                .setEmail("testuser@example.com");
        userStorage.save(user);

        Optional<User> retrievedUser = userStorage.findById(user.getId());

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get()).isEqualTo(user);
    }

    @Test
    public void testDeleteUserById() {
        User user = new User()
                .setName("Test User")
                .setEmail("testuser@example.com");
        userStorage.save(user);

        userStorage.deleteById(user.getId());
        Optional<User> foundUser = userStorage.findById(user.getId());

        assertThat(foundUser).isNotPresent();
    }
}
