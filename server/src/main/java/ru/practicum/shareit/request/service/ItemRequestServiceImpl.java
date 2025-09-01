package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestCreateDto requestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        ItemRequest itemRequest = ItemRequestMapper.toEntity(requestDto);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest saved = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toDto(saved);
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        return itemRequestRepository.findByRequestorOrderByCreatedDesc(user)
            .stream()
            .map(ItemRequestMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        return itemRequestRepository.findByRequestor_IdNotOrderByCreatedDesc(userId)
            .stream()
            .map(ItemRequestMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Запрос не найден"));

        ItemRequestDto request = ItemRequestMapper.toDto(itemRequest);

        List<ItemDto> items = Optional.ofNullable(itemRepository.findAllByRequestId(requestId))
            .orElse(Collections.emptyList())
            .stream()
            .map(ItemMapper::toDto)
            .collect(Collectors.toList());

        request.setItems(items);

        return request;
    }
}
