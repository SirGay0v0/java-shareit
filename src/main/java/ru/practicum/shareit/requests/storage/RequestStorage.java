package ru.practicum.shareit.requests.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.requests.model.Request;

import java.util.List;

@Repository
public interface RequestStorage extends JpaRepository<Request, Long> {

    List<Request> findAllByAuthorIs(Long ownerId);

    Page<Request> findAllByAuthorNotInOrderByCreatedDesc(List<Long> author, PageRequest pageRequest);

    Request findAllById(Long requestId);
}
