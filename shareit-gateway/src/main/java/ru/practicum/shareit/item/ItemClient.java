package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.comments.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Collections;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(long ownerId, ItemRequestDto requestDto) {
        return post("", ownerId, requestDto);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemRequestDto requestDto) {
        return patch("/" + itemId, userId, requestDto);
    }

    public ResponseEntity<Object> getItemById(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItemByUserId(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("", userId, parameters);
    }

    public ResponseEntity<Object> searchItems(String text, int from, int size) {
        if (!StringUtils.hasText(text)) {
            return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
        } else {
            Map<String, Object> parameters = Map.of(
                    "text", text.toLowerCase(),
                    "from", from,
                    "size", size
            );
            return get(
                    "/search?text=" + text + "&from=" + from + "&size=" + size,
                    parameters);
        }
    }

    public ResponseEntity<Object> addComment(CreateCommentDto createDto, long userId, long itemId) {
        return post("/" + itemId + "/comment", userId, createDto);
    }
}
