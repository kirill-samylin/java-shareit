package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest request) {
        if (request == null) return null;

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setRequestor(UserMapper.toDto(request.getRequestor()));
        return dto;
    }

    public static ItemRequest toEntity(ItemRequestDto dto) {
        if (dto == null) return null;
        ItemRequest request = new ItemRequest();
        request.setId(dto.getId());
        request.setDescription(dto.getDescription());
        request.setCreated(dto.getCreated()); // можно оставить null — будет проставлено в сервисе
        return request;
    }
}