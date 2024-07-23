package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.User;

import javax.validation.Valid;
import java.util.List;

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
