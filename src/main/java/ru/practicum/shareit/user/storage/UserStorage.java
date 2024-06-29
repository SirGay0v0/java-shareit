package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStorage extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
