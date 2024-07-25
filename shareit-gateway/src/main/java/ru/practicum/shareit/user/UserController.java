package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.User;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody User user) {
        return client.addUser(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> update(@PathVariable long userId,
                                    @RequestBody User user) {
        return client.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        client.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable long userId) {
        return client.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return client.getAllUsers();
    }
}
