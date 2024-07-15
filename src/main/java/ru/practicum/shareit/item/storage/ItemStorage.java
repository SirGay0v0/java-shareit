package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemStorage extends JpaRepository<Item, Long> {

    Page<Item> findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCaseAndAvailableIsTrue(String name, String description, Pageable pageable);

    Page<Item> findByOwnerId(PageRequest pageRequest, Long ownerId);

    List<Item> findAllByRequestId(Long requestId);

}
