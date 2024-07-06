package ru.practicum.shareit.item.comments.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.comments.model.Comment;

import java.util.List;

@Repository
public interface CommentsStorage extends JpaRepository<Comment, Long> {

    @Query(value = "FROM Comment c WHERE c.item.id = ?1")
    List<Comment> findByItemContaining(Long itemId);
}
