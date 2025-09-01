package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;

public class ItemMapper {

    public static ItemDto toDto(Item item) {
        if (item == null) return null;

        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        if (item.getRequest() != null) {
            dto.setRequest(ItemRequestMapper.toDto(item.getRequest()));
        }

        return dto;
    }

    public static Item toEntity(CreateItemDto dto) {
        if (dto == null) return null;

        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());

        return item;
    }
}
