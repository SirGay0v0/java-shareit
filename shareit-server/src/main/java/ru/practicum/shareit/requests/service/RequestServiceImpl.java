package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.requests.dto.CreateRequestDto;
import ru.practicum.shareit.requests.dto.RequestForUserDto;
import ru.practicum.shareit.requests.dto.ResponseDto;
import ru.practicum.shareit.requests.model.Request;
import ru.practicum.shareit.requests.storage.RequestStorage;
import ru.practicum.shareit.requests.validation.RequestValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final ModelMapper mapper;
    private final RequestStorage storage;
    private final ItemStorage itemStorage;
    private final RequestValidator validator;

    @Override
    public Request create(CreateRequestDto createDto, Long authorId) {
        validator.validateById(authorId);
        Request request = new Request()
                .setAuthor(authorId)
                .setDescription(createDto.getDescription())
                .setCreated(LocalDateTime.now());
        return storage.save(request);
    }

    @Override
    public List<RequestForUserDto> getAllById(Long authorId) {
        validator.validateById(authorId);
        List<Request> requestlist = storage.findAllByAuthorIs(authorId);
        List<RequestForUserDto> resultList = new ArrayList<>();
        for (Request request : requestlist) {
            List<ResponseDto> responsList = itemStorage.findAllByRequestId(request.getId()).stream()
                    .map(response -> mapper.map(response, ResponseDto.class))
                    .collect(Collectors.toList());

            RequestForUserDto requestForUserDto = mapper.map(request, RequestForUserDto.class);
            requestForUserDto.setItems(responsList);
            resultList.add(requestForUserDto);
        }
        return resultList;
    }

    @Override
    public List<RequestForUserDto> getAllFromOtherUsers(Long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        validator.validateById(userId);

        Page<Request> requestPage = storage.findAllByAuthorNotIn(
                List.of(userId), pageRequest.withSort(Sort.Direction.DESC, "created"));

        List<RequestForUserDto> requestDto = requestPage.getContent().stream()
                .map(request -> mapper.map(request, RequestForUserDto.class))
                .collect(Collectors.toList());

        List<Long> listOfIds = requestDto.stream()
                .map(RequestForUserDto::getId)
                .collect(Collectors.toList());

        List<Item> responseAllList = itemStorage.findAllByRequestIdIn(listOfIds);

        for (RequestForUserDto request : requestDto) {
            List<ResponseDto> requestForUserDtoList = responseAllList.stream()
                    .filter(item -> item.getRequestId().equals(request.getId()))
                    .map(item -> mapper.map(item, ResponseDto.class))
                    .collect(Collectors.toList());
            request.setItems(requestForUserDtoList);
        }
        return requestDto;
    }

    @Override
    public RequestForUserDto getRequestById(Long requestId, Long userId) {
        validator.validateRequestAndId(requestId, userId);
        Request request = storage.findAllById(requestId);

        RequestForUserDto requestDto = mapper.map(request, RequestForUserDto.class);
        List<ResponseDto> responseDtoList = itemStorage.findAllByRequestId(requestId).stream()
                .map(response -> mapper.map(response, ResponseDto.class))
                .collect(Collectors.toList());
        requestDto.setItems(responseDtoList);
        return requestDto;
    }
}
