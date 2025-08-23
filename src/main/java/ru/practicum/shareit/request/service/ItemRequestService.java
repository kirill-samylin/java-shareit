package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(Long userId, ItemRequestDto requestDto);

    List<ItemRequestDto> getOwnRequests(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}
